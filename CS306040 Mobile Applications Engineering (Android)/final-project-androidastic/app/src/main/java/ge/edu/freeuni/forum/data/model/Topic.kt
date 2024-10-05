package ge.edu.freeuni.forum.data.model

import com.google.firebase.firestore.DocumentId

data class Topic(
    @DocumentId var id: String = "",
    val name: String = "",
    val subtopics: List<String> = emptyList(),
)