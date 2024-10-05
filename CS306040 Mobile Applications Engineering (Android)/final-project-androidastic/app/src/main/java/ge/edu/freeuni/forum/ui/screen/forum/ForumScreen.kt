package ge.edu.freeuni.forum.ui.screen.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ge.edu.freeuni.forum.data.model.Post
import ge.edu.freeuni.forum.ui.component.TopicItem
import ge.edu.freeuni.forum.ui.component.post.PostItem
import ge.edu.freeuni.forum.ui.screen.AppScaffold
import ge.edu.freeuni.forum.viewmodel.AuthViewModel
import ge.edu.freeuni.forum.viewmodel.ForumViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ForumScreen(
    navController: NavController,
    viewModel: ForumViewModel,
    authViewModel: AuthViewModel
) {
    val topics by viewModel.topics.collectAsState()
    val (posts, setPosts) = remember { mutableStateOf(listOf<Post>()) }

//    val posts by viewModel.posts.collectAsState()
    val authorizedUserId =
        authViewModel.getCurrentUserId();

    val authorizedUser by viewModel.user.collectAsState();

    LaunchedEffect(Unit) {
        viewModel.loadTopics()
        viewModel.loadAllPosts()
        if (authorizedUserId != null) {
            viewModel.loadUser(authorizedUserId)
        }
        viewModel.loadSubTopics()
        if (authorizedUserId != null) {
            viewModel.getUserWithUserId(authorizedUserId)
        }

        viewModel.fetchPosts { fetchedPosts ->
            setPosts(fetchedPosts)}
    }

    AppScaffold(navController = navController, authViewModel = authViewModel) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(247, 243, 243, 255))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Posts (${posts.size})",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                items(posts) { post ->
                    authorizedUser?.let {
                        PostItem(
                            post = post,
                            onReply = { post, reply -> viewModel.replyToPost(post.id, reply) },
                            onViewComments = { postId -> viewModel.getCommentsForPost(postId) },
                            user = it,
                            onDelete = { postId ->
                                    viewModel.deletePostOrComment(postId)
                                setPosts(posts.filter {p -> p.id != postId})
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}