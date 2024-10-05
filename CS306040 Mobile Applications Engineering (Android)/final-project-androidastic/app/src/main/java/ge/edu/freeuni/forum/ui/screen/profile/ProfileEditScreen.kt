package ge.edu.freeuni.forum.ui.screen.profile

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseUser
import ge.edu.freeuni.forum.R
import ge.edu.freeuni.forum.viewmodel.AuthViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileEditScreen(navController: NavController, authViewModel: AuthViewModel) {
    val user: FirebaseUser? = authViewModel.user.value
    val displayName = remember { mutableStateOf(user?.displayName ?: "") }
    val username = remember { mutableStateOf(authViewModel.username.value ?: "") }
    val profilePicture = remember { mutableStateOf(user?.photoUrl?.toString() ?: "") }

    val context = LocalContext.current
    val permissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profilePicture.value = uri.toString()
            authViewModel.uploadProfilePicture(uri) { downloadUrl ->
                if (downloadUrl != null) {
                    authViewModel.updateUserProfile(displayName.value, downloadUrl, username.value)
                } else {
                    // Handle error
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        .size(70.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (permissionState.status.isGranted) {
                                launcher.launch("image/*")
                            } else {
                                permissionState.launchPermissionRequest()
                            }
                        }
                )

                Spacer(modifier = Modifier.width(16.dp))


                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user?.email ?: "user@example.com",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colors.primary,
                        unfocusedIndicatorColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = displayName.value,
                    onValueChange = { displayName.value = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colors.primary,
                        unfocusedIndicatorColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.updateUserProfile(
                    displayName.value,
                    profilePicture.value,
                    username.value
                )
                navController.navigateUp()
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp,
                hoveredElevation = 4.dp,
                focusedElevation = 4.dp
            )
        ) {
            Text(text = "Save Changes", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigateUp()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp,
                hoveredElevation = 4.dp,
                focusedElevation = 4.dp
            )
        ) {
            Text(text = "Cancel", color = Color.White, fontSize = 16.sp)
        }
    }
}
