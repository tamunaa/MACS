package ge.edu.freeuni.forum.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import ge.edu.freeuni.forum.data.model.Post
import ge.edu.freeuni.forum.data.model.Topic
import ge.edu.freeuni.forum.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ForumViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance().reference

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics

    private val _subTopics = MutableStateFlow<List<String>>(emptyList())

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _currentUser = MutableStateFlow<User?>(null)

    private val _comments = MutableStateFlow<List<Post>>(emptyList())
    val comments: StateFlow<List<Post>> = _comments

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> get() = _userPosts

    private val _selectedTopicId = MutableStateFlow<String?>(null)
    val selectedTopicId: StateFlow<String?> = _selectedTopicId

    private val _postsForSelectedTopic = MutableStateFlow<List<Post>>(emptyList())
    val postsForSelectedTopic: StateFlow<List<Post>> = _postsForSelectedTopic

    private val _isLoadingPosts = MutableStateFlow(false)
    val isLoadingPosts: StateFlow<Boolean> = _isLoadingPosts
    fun selectTopic(topicId: String) {
        _selectedTopicId.value = topicId
        loadPostsForTopic(topicId)
    }

    fun loadPostsForTopic(topicId: String) {
        viewModelScope.launch {
            _isLoadingPosts.value = true
            val postsRef = db.child("posts")
            val dataSnapshot = postsRef.get().await()

            if (dataSnapshot.exists()) {
                val posts = mutableListOf<Post>()
                dataSnapshot.children.forEach { postSnapshot ->
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post?.topicId == topicId) {
                        post.let { posts.add(it) }
                    }
                }
                _postsForSelectedTopic.value = posts
                _isLoadingPosts.value = false
            } else {
                println("No posts found for topic $topicId")
            }
        }
        println("Posts for topic $topicId loaded," +
                " length of posts ${posts.value.size}")
    }

    fun loadSubTopics(parentTopicId: String? = null) {
        viewModelScope.launch {
            try {
                val topicsRef = db.child("topics")
                    .child(parentTopicId ?: "")
                    .child("subtopics")

                topicsRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()){

                        val subTopics = mutableListOf<String>()
                        dataSnapshot.children.forEach { topicSnapshot ->
                            val topic = topicSnapshot.getValue(String::class.java)
                            topic?.let { subTopics.add(it) }
                        }
                        _subTopics.value = subTopics
                    }else{
                        println("No topics found")
                    }

                }.addOnFailureListener {
                    println("Error: ${it.message}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                println("Error: ${e.message}")
            }
        }
    }

    fun fetchPosts(callback: (List<Post>) -> Unit) {
        viewModelScope.launch {
            val postsRef = db.child("posts")

            postsRef.get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()){

                    val posts = mutableListOf<Post>()
                    dataSnapshot.children.forEach { postSnapshot ->
                        val post = postSnapshot.getValue(Post::class.java)
                        post?.let {
                            posts.add(it)
                        }

                    }
                    callback(posts)
                }else{
                    println("No posts found")
                }

            }.addOnFailureListener {
                println("Error: ${it.message}")
            }
        }
    }

    fun getUserWithUserId(userId: String){
        viewModelScope.launch {
            try {
                val userRef = db.child("users").child(userId)

                userRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()){

                        val user = dataSnapshot.getValue(User::class.java)
                        _user.value = user
                    }else{
                        println("No user found")
                    }

                }.addOnFailureListener {
                    println("Error: ${it.message}")
                }

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getUserById(userId: String): User? {
        getUserWithUserId(userId)
        return user.value
    }

    fun loadTopics(){
        viewModelScope.launch {
            try {
                val topicsRef = db.child("topics")

                topicsRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()){

                        val topics = mutableListOf<Topic>()
                        dataSnapshot.children.forEach { topicSnapshot ->
                            val topic = topicSnapshot.getValue(Topic::class.java)
                            topic?.let { topics.add(it) }
                        }
                        _topics.value = topics
                    }else{
                        println("No topics found when loading topics")
                    }

                }.addOnFailureListener {
                    println("Error: ${it.message}")
                }

            } catch (e: Exception) {
                // Handle error
            }

        }
    }
    fun loadAllPosts(){
        viewModelScope.launch {
            try {
                val postsRef = db.child("posts")

                postsRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()){

                        val posts = mutableListOf<Post>()
                        dataSnapshot.children.forEach { postSnapshot ->
                            val post = postSnapshot.getValue(Post::class.java)
                            post?.let {
                                posts.add(it)
                            }

                        }
                        _posts.value = posts
                    }else{
                        println("No posts found")
                    }

                }.addOnFailureListener {
                    println("Error: ${it.message}")
                }

            } catch (e: Exception) {
                // Handle error
            }

        }
    }

    fun getCommentsForPost(postId: String) {
        viewModelScope.launch {
            try{
                val commentsRef = db.child("posts").child(postId).child("comments")

                commentsRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()){

                        val comments = mutableListOf<Post>()
                        dataSnapshot.children.forEach { commentSnapshot ->
                            val comment = commentSnapshot.getValue(Post::class.java)
                            comment.let {
                                if (it != null) {
                                    comments.add(it)
                                }
                            }
                        }
                        _comments.value = comments
                    }else{
                        println("No comments found")
                    }

                }.addOnFailureListener {
                    println("Error: ${it.message}")
                }

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadUser(userId: String){
        viewModelScope.launch {
            try {
                val userRef = db.child("users").child(userId)

                userRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()){

                        val user = dataSnapshot.getValue(User::class.java)
                        _currentUser.value = user
                    }else{
                        println("No user found")
                    }

                }.addOnFailureListener {
                    println("Error: ${it.message}")
                }

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun replyToPost(postId: String, comment: Post) {
        viewModelScope.launch {
            val commentRef = db.child("posts").child(postId).child("comments").push()
            val commentId = commentRef.key

            commentRef.setValue(comment)
                .addOnSuccessListener {
                    println("Comment added successfully to post $postId")
                }
                .addOnFailureListener { e ->
                    println("Error adding comment to post $postId: ${e.message}")
                }
            }
    }


    fun createPost(newPost: Post) {
        viewModelScope.launch {
            try {
                val postRef = db.child("posts").push()
                val postId = postRef.key

                postRef.setValue(postId?.let { newPost.copy(id = it) }).addOnSuccessListener {
                    println("Post created successfully")
                }.addOnFailureListener {
                    println("Error: ${it.message}")
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    suspend fun getPostsCreatedByUser(userId: String): List<Post> = withContext(Dispatchers.IO) {
        return@withContext try {
            val postsRef = db.child("posts")
            val dataSnapshot = postsRef.get().await()

            if (dataSnapshot.exists()) {
                val userPosts = mutableListOf<Post>()
                dataSnapshot.children.forEach { postSnapshot ->
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post?.authorId == userId) {
                        post.let { userPosts.add(it) }
                    }
                }
                userPosts
            } else {
                println("No posts found")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            emptyList()
        }
    }

    fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            val posts = getPostsCreatedByUser(userId)
            _userPosts.value = posts
        }
    }

    fun deletePostOrComment(id: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val postRef = db.child("posts").child(id)
                postRef.removeValue().addOnSuccessListener {
                    onSuccess();
                    println("Post deleted successfully")
                }.addOnFailureListener {
                    println("Error: ${it.message}")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }


}