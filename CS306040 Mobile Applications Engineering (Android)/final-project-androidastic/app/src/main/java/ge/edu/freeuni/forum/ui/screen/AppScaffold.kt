package ge.edu.freeuni.forum.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import ge.edu.freeuni.forum.R
import ge.edu.freeuni.forum.data.model.User
import ge.edu.freeuni.forum.viewmodel.AuthViewModel

@Composable
fun AppScaffold(
    navController: NavController,
    authViewModel: AuthViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val currentUserId = authViewModel.getCurrentUserId()
    val user by produceState<User?>(initialValue = null) {
        if (currentUserId != null) {
            value = authViewModel.getUserById(currentUserId)
        }
    }
    val profilePicture = user?.profilePictureUrl

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        "home" -> "Home"
        "users" -> "Users"
        "newpost" -> "New Post"
        "search" -> "Search"
        "profile/{userId}" -> "Profile"
        else -> "Home"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (profilePicture != null) {
                            Image(
                                painter = if (profilePicture.isNotEmpty()) rememberImagePainter(data = profilePicture) else painterResource(
                                    id = R.drawable.ic_user
                                ),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        // Handle profile picture change
                                    }
                            )
                        }
                        Text(title)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    IconButton(onClick = { navController.navigate("newpost") }) {
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "Create Post",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                backgroundColor = Color(0xD8E0DDE9)
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xBF7C4DFF)
            ) {
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.house),
                            contentDescription = "Home",
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") }
                )
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.users),
                            contentDescription = "People",
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    selected = currentRoute == "users",
                    onClick = { navController.navigate("users") }
                )
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.create),
                            contentDescription = "Create",
                            modifier = Modifier.size(40.dp)
                        )

                    },
                    selected = currentRoute == "newpost",
                    onClick = { navController.navigate("newpost") }
                )
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp)
                        )

                    },
                    selected = currentRoute?.startsWith("profile") == true,
                    onClick = { navController.navigate("profile/${authViewModel.getCurrentUserId()}") }
                )
            }
        },
        content = content
    )
}
