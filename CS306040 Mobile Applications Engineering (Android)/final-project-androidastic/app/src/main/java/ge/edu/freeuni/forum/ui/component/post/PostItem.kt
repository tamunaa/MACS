package ge.edu.freeuni.forum.ui.component.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ge.edu.freeuni.forum.data.model.Post
import ge.edu.freeuni.forum.viewmodel.ForumViewModel

import android.util.Patterns
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import ge.edu.freeuni.forum.R
import ge.edu.freeuni.forum.data.model.User
import java.util.*

@Composable
fun PostItem(
    post: Post,
    onReply: (Post, Post) -> Unit,
    onViewComments: (String) -> Unit,
    user: User,
    onDelete: (String) -> Unit
) {
    var isReplying by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var showComments by remember { mutableStateOf(false) }

    println("useeeeeeeeeeeeer: $user")

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(224, 188, 224, 255),
            Color(64, 230, 205, 201),
            Color(25, 63, 175, 255)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(gradient, shape = RoundedCornerShape(12.dp))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 6.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // User Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(182, 52, 182, 255),
                                        Color(214, 1, 86, 163),
                                        Color(25, 63, 175, 255)
                                    )
                                )
                            )
                    ) {
                        Image(
                            painter =  painterResource(
                                id = R.drawable.ic_user
                            ),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = post.authorDisplayName,
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${post.createdAt}",
                            style = MaterialTheme.typography.caption,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    if(user.moderator){
                        Button(
                            onClick = {
                                onDelete(post.id)
                                      },
                            ) {

                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Delete",
                            )
                        }
                    }
                }
                Text(
                    text = post.topicId,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { isReplying = !isReplying }) {
                            Icon(
                                painter = painterResource(id = R.drawable.reply),
                                contentDescription = "Reply",
                                tint = MaterialTheme.colors.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reply",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.primary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                showComments = !showComments
                                onViewComments(post.id)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "View Comments",
                                tint = MaterialTheme.colors.secondary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showComments) "Hide Comments" else "View Comments",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.secondary
                        )
                    }
                }

                if (isReplying) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Your Reply") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        textStyle = MaterialTheme.typography.body1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onReply(
                                post,
                                Post(
                                    authorDisplayName = user.displayName,
                                    authorId = user.userId,
                                    content = commentText,
                                    topicId = post.topicId,
                                )
                            )
                            commentText = ""
                            isReplying = false
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                    ) {
                        Text("Submit")
                    }
                }

                if (showComments) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CommentsSection(postId = post.id)
                }
            }
        }
    }
}

@Composable
fun CommentsSection(postId: String, viewModel: ForumViewModel = viewModel()) {
    LaunchedEffect(postId) {
        viewModel.getCommentsForPost(postId)
    }
    val comments by viewModel.comments.collectAsState()
    var displayedComments by remember { mutableStateOf(5) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.comment),
                contentDescription = "Comments",
                tint = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Comments (${comments.size})",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
        }

        if (comments.isEmpty()) {
            Text(
                text = "No comments yet. Be the first to comment!",
                style = MaterialTheme.typography.body2,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            comments.take(displayedComments).forEachIndexed { index, comment ->
                CommentItem(comment = comment)
                if (index < displayedComments - 1 && index < comments.lastIndex) {
                    Divider(
                        color = Color.LightGray,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            if (displayedComments < comments.size) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { displayedComments = minOf(displayedComments + 5, comments.size) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Load More"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Load More")
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Post) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // User Avatar (placeholder)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.2f))
        ) {
            Image(
                painter =  painterResource(
                    id = R.drawable.ic_user
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.authorDisplayName,
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
                Text(
                    text = "${comment.createdAt}",
                    style = MaterialTheme.typography.caption
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            ClickableText(
                text = detectLinks(comment.content),
                style = MaterialTheme.typography.body2,
                onClick = { offset ->
                    detectLinks(comment.content)
                        .getStringAnnotations("URL", offset, offset)
                        .firstOrNull()?.let { annotation ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                            context.startActivity(intent)
                        }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


fun detectLinks(text: String): AnnotatedString {
    val annotatedString = buildAnnotatedString {
        append(text)
        val matcher = Patterns.WEB_URL.matcher(text)
        var lastIndex = 0
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            if (start > lastIndex) {
                addStyle(SpanStyle(color = Color.Black), lastIndex, start)
            }
            addStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline), start, end)
            addStringAnnotation("URL", matcher.group(), start, end)
            lastIndex = end
        }
        if (lastIndex < text.length) {
            addStyle(SpanStyle(color = Color.Black), lastIndex, text.length)
        }
    }
    return annotatedString
}
