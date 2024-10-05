package ge.edu.freeuni.forum.data.model
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import java.util.Date

data class Post(
    @DocumentId var id: String = "",
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val authorDisplayName: String = "",

    val topicId: String = "",
    val createdAt: Date = Date(),
    val links: List<String> = emptyList(),
    val parentPostId: String? = null
)
