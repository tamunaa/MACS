package ge.edu.freeuni.forum.ui.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import ge.edu.freeuni.forum.R
import ge.edu.freeuni.forum.data.model.Post
import ge.edu.freeuni.forum.data.model.User
import ge.edu.freeuni.forum.ui.component.post.PostItem
import ge.edu.freeuni.forum.ui.screen.AppScaffold
import ge.edu.freeuni.forum.viewmodel.AuthViewModel
import ge.edu.freeuni.forum.viewmodel.ForumViewModel

@Composable
fun UserProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    forumViewModel: ForumViewModel,
    userId: String?
) {
    val currentUserId = authViewModel.getCurrentUserId()
    val (posts, setPosts) = remember { mutableStateOf(listOf<Post>()) }
    forumViewModel.fetchPosts { fetchedPosts ->
        setPosts(fetchedPosts)}

    val user by produceState<User?>(initialValue = null) {
        if (userId != null) {
            value = authViewModel.getUserById(userId)
        }
    }

    user?.let {
        UserProfileContent(
            navController = navController,
            user = it,
            authViewModel = authViewModel,
            forumViewModel = forumViewModel,
            isCurrentUser = currentUserId == userId,
            posts, setPosts
        )
    }
}

@Composable
fun UserProfileContent(
    navController: NavController,
    user: User,
    authViewModel: AuthViewModel,
    forumViewModel: ForumViewModel,
    isCurrentUser: Boolean,
    posts: List<Post>, setPosts: (List<Post>) -> Unit
) {
    val displayName = user.displayName
    val profilePicture = remember { mutableStateOf(user.profilePictureUrl) }
    val username = user.username

    AppScaffold(navController = navController, authViewModel = authViewModel) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painter = if (profilePicture.value.isNotEmpty()) rememberImagePainter(data = profilePicture.value) else painterResource(
                            id = R.drawable.ic_user
                        ),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .clickable {
                            }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = username,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "0 followers",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1 following",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (isCurrentUser) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            navController.navigate("profile_edit")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp,
                            hoveredElevation = 4.dp,
                            focusedElevation = 4.dp
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_create),
                            contentDescription = "Edit Profile",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Edit profile", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            authViewModel.signOut()
                            navController.navigate("login")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAF2C2C)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp,
                            hoveredElevation = 4.dp,
                            focusedElevation = 4.dp
                        )
                    ) {
                        Text(text = "Sign Out", color = Color.White, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Credentials & Highlights",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Divider()

                    CredentialItem(label = "Joined " + user.createdAt, icon = R.drawable.ic_calendar)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ExpandableUserPosts(userId = user.userId, forumViewModel = forumViewModel, user = user, setPosts = setPosts)
        }
    }
}

@Composable
fun CredentialItem(label: String, icon: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ExpandableUserPosts(userId: String, forumViewModel: ForumViewModel, user: User, setPosts: (List<Post>) -> Unit) {
    forumViewModel.loadUserPosts(userId)
    val posts by forumViewModel.userPosts.collectAsState(initial = emptyList())

    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp,
                hoveredElevation = 4.dp,
                focusedElevation = 4.dp
            )
        ) {
            val icon =
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
            val text = if (expanded) "Hide Posts (${posts.size})" else "Show Posts (${posts.size})"

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn {
                items(posts) { post ->
                    PostItem(
                        post = post,
                        onReply = { post, reply -> forumViewModel.replyToPost(post.id, reply) },
                        onViewComments = { postId -> forumViewModel.getCommentsForPost(postId) },
                        user = user,
                        onDelete = { postId ->
                            forumViewModel.deletePostOrComment(postId)
                            setPosts(posts.filter { p -> p.id != postId })
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}