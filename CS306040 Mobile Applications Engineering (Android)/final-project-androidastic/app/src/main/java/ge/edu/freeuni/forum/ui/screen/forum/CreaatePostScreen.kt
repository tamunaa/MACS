package ge.edu.freeuni.forum.ui.screen.forum

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ge.edu.freeuni.forum.data.model.Post
import ge.edu.freeuni.forum.data.model.User
import ge.edu.freeuni.forum.ui.component.post.CompactCreatePost
import ge.edu.freeuni.forum.ui.component.post.ExpandedCreatePost
import ge.edu.freeuni.forum.ui.component.post.SuccessMessage
import ge.edu.freeuni.forum.ui.screen.AppScaffold
import ge.edu.freeuni.forum.viewmodel.AuthViewModel
import ge.edu.freeuni.forum.viewmodel.ForumViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CreatePostScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    forumViewModel: ForumViewModel,
    onPostCreated: (Post) -> Unit,
    currentUserId: String
) {
    var expanded by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val currentUser by produceState<User?>(initialValue = null) {
        value = authViewModel.getUserFromDB(currentUserId)
    }

    val allTopics = forumViewModel.topics.value
    var subtopics = emptyList<String>()

    for (topic in allTopics) {
        subtopics += topic.subtopics
    }

    AppScaffold(navController = navController, authViewModel = authViewModel) { paddingValues ->
        Scaffold(
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(innerPadding)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column {
                        if (!expanded) {
                            CompactCreatePost(
                                onExpand = { expanded = true }
                            )
                        } else {
                            ExpandedCreatePost(
                                onPostCreated = { post ->
                                    val postWithDisplayName = post.copy(
                                        authorDisplayName = currentUser?.displayName ?: "Unknown"
                                    )
                                    onPostCreated(postWithDisplayName)
                                    expanded = false
                                    showSuccessMessage = true
                                    coroutineScope.launch {
                                        delay(2000)
                                        showSuccessMessage = false
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                },
                                onCancel = { expanded = false },
                                topics = subtopics,
                                currentUserId = currentUserId,
                                user = currentUser
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showSuccessMessage,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    SuccessMessage()
                }
            }
        }
    }

}
