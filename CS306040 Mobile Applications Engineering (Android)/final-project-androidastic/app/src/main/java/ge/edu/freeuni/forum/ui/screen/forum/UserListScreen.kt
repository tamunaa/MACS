package ge.edu.freeuni.forum.ui.screen.forum

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import ge.edu.freeuni.forum.R
import ge.edu.freeuni.forum.data.model.User
import ge.edu.freeuni.forum.ui.screen.AppScaffold
import ge.edu.freeuni.forum.viewmodel.AuthViewModel

@Composable
fun UserListScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    authViewModel.fetchUsers()
    val users by authViewModel.users.collectAsState()


    AppScaffold(navController = navController, authViewModel = authViewModel) { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HeaderSection()
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    UserItem(user = user, navController = navController)
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "Here you can find all registered users.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun UserItem(user: User, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("profile/${user.userId}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = if (user.profilePictureUrl.isNotEmpty()) rememberImagePainter(data = user.profilePictureUrl) else painterResource(
                    id = R.drawable.ic_user
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = user.username, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material.Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
