package ge.edu.freeuni.forum.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import ge.edu.freeuni.forum.R
import ge.edu.freeuni.forum.data.model.User
import ge.edu.freeuni.forum.ui.component.post.PostItem
import ge.edu.freeuni.forum.ui.screen.AppScaffold
import ge.edu.freeuni.forum.viewmodel.AuthViewModel
import ge.edu.freeuni.forum.viewmodel.ForumViewModel
import okhttp3.internal.wait

@Composable
fun SearchBar(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    forumViewModel: ForumViewModel
) {
    val query = remember { mutableStateOf("") }
    val topics = forumViewModel.topics.value ?: emptyList()
    val searchResults = authViewModel.searchResults
    val postsForSelectedTopic by forumViewModel.postsForSelectedTopic.collectAsState()
    val isLoadingPosts by forumViewModel.isLoadingPosts.collectAsState()


    AppScaffold(navController = navController, authViewModel = authViewModel) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            TextField(
                value = query.value,
                onValueChange = {
                    query.value = it
                    authViewModel.performSearch(it)
                },
                placeholder = { Text("Search...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(searchResults.value ?: emptyList()) { result ->
                    SearchResultItem(result = result, navController = navController)
                }
                items(topics) { topic ->
                    TopicItem(
                        topic = topic,
                        onTopicClick = { topicId -> forumViewModel.loadSubTopics(topicId) },
                        onSubtopicClick = { forumViewModel.loadPostsForTopic(topic.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    when {
                        isLoadingPosts -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentSize(Alignment.Center)
                            )
                            Text(
                                "Loading posts...",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                        postsForSelectedTopic.isEmpty() -> {
                            Text(
                                "No posts available for this topic.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                        else -> {
                            Text(
                                "Posts for selected topic:",
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                items(postsForSelectedTopic) { post ->
                    forumViewModel.getUserById(post.authorId)?.let {
                        PostItem(
                            post = post,
                            onReply = { post, reply -> forumViewModel.replyToPost(post.id, reply) },
                            onViewComments = { postId -> forumViewModel.getCommentsForPost(postId) },
                            user = it,
                            onDelete = { postId ->
                                forumViewModel.deletePostOrComment(postId)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            LazyColumn {

            }
        }
    }
}

@Composable
fun SearchResultItem(result: User, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                navController.navigate("profile/${result.userId}")
            },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(
                    data = result.profilePictureUrl,
                    builder = {
                        placeholder(R.drawable.ic_user)
                        error(R.drawable.ic_user)
                    }
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = result.username,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
