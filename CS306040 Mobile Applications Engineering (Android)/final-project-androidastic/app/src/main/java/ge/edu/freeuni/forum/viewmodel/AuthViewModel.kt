package ge.edu.freeuni.forum.viewmodel

import android.app.appsearch.SearchResult
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import ge.edu.freeuni.forum.data.model.User
import ge.edu.freeuni.forum.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> get() = _user

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> get() = _username

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> get() = _displayName

    val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    private val _searchResults = MutableLiveData<List<User>>()
    val searchResults: LiveData<List<User>> = _searchResults

    init {
        _user.value = repository.getCurrentUser()
        fetchUsername()
        fetchDisplayName()
        fetchUsers()
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun fetchUsername() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val username = repository.getUsername(uid)
                _username.value = username
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to fetch username", e)
                _errorMessage.value = e.message
            }
        }
    }

    private fun fetchDisplayName() {
        val user = FirebaseAuth.getInstance().currentUser
        _displayName.value = user?.displayName
    }

    fun fetchUsers() {
        viewModelScope.launch {
            val userList = repository.getAllUsers()
            _users.value = userList
        }
    }

    fun createUser(email: String, password: String, displayName: String, username: String) {
        viewModelScope.launch {
            try {
                val createdUser = repository.createUser(email, password, displayName, username)

                _user.value = createdUser
                _username.value = username
                _displayName.value = displayName
                clearErrorMessage()
            } catch (e: Exception) {
                _errorMessage.value = when (e) {
                    is FirebaseAuthUserCollisionException -> "The email address is already in use."
                    is FirebaseAuthWeakPasswordException -> "The password is too weak."
                    else -> e.message
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val loggedInUser = repository.loginUser(email, password)
                _user.value = loggedInUser
                _displayName.value = loggedInUser?.displayName
                clearErrorMessage()
            } catch (e: Exception) {
                _user.value = null
                _errorMessage.value = when (e) {
                    is FirebaseAuthInvalidUserException -> "No user found with this email."
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect password."
                    else -> e.message
                }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                repository.resetPassword(email)
                clearErrorMessage()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val googleUser = repository.signInWithGoogle(idToken)
                _user.value = googleUser
                _displayName.value = googleUser?.displayName
                clearErrorMessage()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("427760656686-pmfvdv82kplmbjs9v1clmvib2oafkbio.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options)
    }

    fun signOut() {
        repository.logout()
        _user.value = null
        _username.value = null
        _displayName.value = null
        clearErrorMessage()
    }

    suspend fun fetchUserFromDB(): User? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        return try {
            val user = repository.getUserFromDB(uid)
            _user.value = FirebaseAuth.getInstance().currentUser
            _username.value = user?.username
            _displayName.value = user?.displayName
            user
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Failed to fetch user from database", e)
            _errorMessage.value = e.message
            null
        }
    }

    fun updateUserProfile(displayName: String, profilePictureUrl: String?, username: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .apply {
                    if (profilePictureUrl != null) {
                        setPhotoUri(Uri.parse(profilePictureUrl))
                    }
                }
                .build()

            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "User profile updated.")
                    _user.value = user
                    _displayName.value = displayName

                    // Update user in database
                    viewModelScope.launch {
                        try {
                            val updatedUser = User(
                                userId = user.uid,
                                email = user.email ?: "",
                                username = username,
                                displayName = displayName,
                                profilePictureUrl = profilePictureUrl.toString()
                            )
                            repository.saveUserToDB(updatedUser)
                            _username.value = username
                        } catch (e: Exception) {
                            Log.e("AuthViewModel", "Failed to update user in database", e)
                            _errorMessage.value = e.message
                        }
                    }
                } else {
                    _errorMessage.value = task.exception?.message
                }
            }
        }
    }

    fun uploadProfilePicture(uri: Uri, onComplete: (String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val storageRef =
                FirebaseStorage.getInstance().reference.child("profile_pictures/${user.uid}")

            viewModelScope.launch {
                try {
                    val uploadTask = storageRef.putFile(uri).await()
                    val downloadUrl = storageRef.downloadUrl.await().toString()
                    onComplete(downloadUrl)
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Profile picture upload failed", e)
                    onComplete(null)
                }
            }
        } else {
            onComplete(null)
        }
    }

    suspend fun getUserFromDB(uid: String): User? {
        return repository.getUserFromDB(uid)
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>, authViewModel: AuthViewModel) {
        try {
            if (task.isSuccessful) {
                val account = task.getResult(Exception::class.java)
                if (account != null) {
                    Log.d("LoginScreen", "Google sign-in successful, ID Token: ${account.idToken}")
                    account.idToken?.let {
                        authViewModel.signInWithGoogle(it)
                    }
                } else {
                    Log.e("LoginScreen", "Google sign-in failed: account is null")
                    authViewModel._errorMessage.value = "Google sign-in failed: account is null"
                }
            } else {
                Log.e("LoginScreen", "Task is not successful", task.exception)
                authViewModel._errorMessage.value =
                    "Google sign-in failed: ${task.exception?.message}"
            }
        } catch (e: Exception) {
            Log.e("LoginScreen", "Google sign-in failed", e)
            authViewModel._errorMessage.value = "Google sign-in failed: ${e.message}"
        }
    }

    fun getUserById(userId: String): User? = runBlocking {
        repository.getUserFromDB(userId)
    }

    fun performSearch(query: String) {
        viewModelScope.launch {
            val results = _users.value.filter { it.username.contains(query, ignoreCase = true) }
            _searchResults.value = results
        }
    }
}
