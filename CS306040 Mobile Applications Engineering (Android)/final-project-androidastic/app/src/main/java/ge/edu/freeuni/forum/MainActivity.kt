package ge.edu.freeuni.forum

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import ge.edu.freeuni.forum.data.repository.AuthRepository
import ge.edu.freeuni.forum.ui.component.SearchBar
import ge.edu.freeuni.forum.ui.screen.AppScaffold
import ge.edu.freeuni.forum.ui.screen.auth.LoginScreen
import ge.edu.freeuni.forum.ui.screen.auth.RegisterScreen
import ge.edu.freeuni.forum.ui.screen.auth.ResetPasswordScreen
import ge.edu.freeuni.forum.ui.screen.forum.CreatePostScreen
import ge.edu.freeuni.forum.ui.screen.forum.ForumScreen
import ge.edu.freeuni.forum.ui.screen.forum.UserListScreen
import ge.edu.freeuni.forum.ui.screen.profile.ProfileEditScreen
import ge.edu.freeuni.forum.ui.screen.profile.UserProfileScreen
import ge.edu.freeuni.forum.ui.theme.ForumTheme
import ge.edu.freeuni.forum.viewmodel.AuthViewModel
import ge.edu.freeuni.forum.viewmodel.ForumViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForumTheme {
                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()
                val authRepository = AuthRepository(auth)
                val authViewModel: AuthViewModel = AuthViewModel(authRepository)
                val forumViewModel: ForumViewModel = ForumViewModel()
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { LoginScreen(navController, authViewModel) }
                        composable("register") { RegisterScreen(navController, authViewModel) }
                        composable("home") {
                            ForumScreen(
                                navController,
                                forumViewModel,
                                authViewModel
                            )
                        }
                        composable("profile_edit") {
                            AppScaffold(
                                navController = navController,
                                authViewModel = authViewModel
                            ) { paddingValues ->
                                ProfileEditScreen(
                                    navController,
                                    authViewModel
                                )
                            }
                        }

                        composable("newpost") {
                            authViewModel.getCurrentUserId()?.let { it1 ->
                                CreatePostScreen(
                                    navController,
                                    authViewModel,
                                    forumViewModel,
                                    onPostCreated = { newPost ->
                                        forumViewModel.createPost(newPost)
                                    },
                                    currentUserId = it1
                                )
                            }
                        }
                        composable("profile/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            UserProfileScreen(navController, authViewModel, forumViewModel, userId)
                        }

                        composable("search") {
                            SearchBar(navController, authViewModel, forumViewModel)
                        }

                        composable("users") {
                            UserListScreen(navController, authViewModel)
                        }
                        composable("resetPassword") {
                            ResetPasswordScreen(
                                navController,
                                authViewModel
                            )
                        }
                    }
                }
//                val viewModel: ForumViewModel by viewModels()
//                SuccessScreen(viewModel)
            }
        }
    }
}