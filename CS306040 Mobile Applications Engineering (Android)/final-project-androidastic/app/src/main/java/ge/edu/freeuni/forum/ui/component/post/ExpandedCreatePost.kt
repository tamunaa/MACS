package ge.edu.freeuni.forum.ui.component.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import ge.edu.freeuni.forum.R
import ge.edu.freeuni.forum.data.model.Post
import ge.edu.freeuni.forum.data.model.User

@Composable
fun ExpandedCreatePost(
    onPostCreated: (Post) -> Unit,
    onCancel: () -> Unit,
    topics: List<String>,
    currentUserId: String,
    user: User?
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTopicIndex by remember { mutableStateOf(0) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colors.surface, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (user != null) {
                Image(
                    painter = if (user.profilePictureUrl.isNotEmpty()) rememberImagePainter(data = user.profilePictureUrl) else painterResource(
                        id = R.drawable.ic_user
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (user != null) {
                Text(
                    user.username,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Start your question with \"What\", \"How\", \"Why\", etc.") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Say more about your question (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            textStyle = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = topics.getOrNull(selectedTopicIndex) ?: "",
                onValueChange = { },
                label = { Text("Topic") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Select Topic",
                        modifier = Modifier.clickable { isDropdownExpanded = true }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                topics.forEachIndexed { index, topic ->
                    DropdownMenuItem(onClick = {
                        selectedTopicIndex = index
                        isDropdownExpanded = false
                    }) {
                        Text(text = topic)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Cancel", color = MaterialTheme.colors.error)
            }
            Button(
                onClick = {
                    val newPost = Post(
                        title = title,
                        content = content,
                        authorId = currentUserId,
                        topicId = topics[selectedTopicIndex]
                    )
                    onPostCreated(newPost)
                },
                enabled = title.isNotBlank() && selectedTopicIndex >= 0,
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text("Add Question")
            }
        }
    }
}
