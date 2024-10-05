package ge.edu.freeuni.forum.data.model
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class User(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Date = Date(),
    val profilePictureUrl: String = "",
    val moderator: Boolean = false,
)