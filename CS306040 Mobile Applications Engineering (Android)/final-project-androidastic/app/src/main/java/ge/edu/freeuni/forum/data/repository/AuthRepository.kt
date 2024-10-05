package ge.edu.freeuni.forum.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import ge.edu.freeuni.forum.data.model.User
import kotlinx.coroutines.tasks.await
import java.util.Date

class AuthRepository(private val auth: FirebaseAuth) {
    private val db = FirebaseDatabase.getInstance().reference

    suspend fun createUser(
        email: String,
        password: String,
        displayName: String,
        username: String
    ): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build())
            ?.await()
        user?.let {
            val userToSave = User(
                userId = it.uid,
                email = email,
                username = username,
                displayName = displayName,
                createdAt = Date(),
                moderator = false,
            )
            saveUserToDB(userToSave)
        }
        return user
    }

    suspend fun saveUserToDB(user: User) {
        val userRef = db.child("users").child(user.userId)
        userRef.setValue(user).await()
    }

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val userToSave = result.user?.let {
            it.email?.let { it1 ->
                User(
                    userId = it.uid,
                    email = it1,
                    username = it1,
                    displayName = "",
                    createdAt = Date(),
                    moderator = false,
                    profilePictureUrl = result.user!!.photoUrl.toString()
                )
            }
        }
        if (userToSave != null) {
            saveUserToDB(userToSave)
        }

        return result.user
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun logout() = auth.signOut()

    suspend fun getUsername(uid: String): String? {
        val userRef = db.child("users").child(uid).child("username")
        return userRef.get().await().getValue(String::class.java)
    }

    suspend fun getUserFromDB(uid: String): User? {
        val userRef = db.child("users").child(uid)
        return userRef.get().await().getValue(User::class.java)
    }

    suspend fun getAllUsers(): List<User> {
        val usersRef = db.child("users")
        val snapshot = usersRef.get().await()
        return snapshot.children.mapNotNull { it.getValue(User::class.java) }
    }
}
