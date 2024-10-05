import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color

@Composable
fun AppInfoItem(
    appName: String,
    metaInfo: String,
    additonalInfo: String,
    appIconId: Int,
    onInstallClicked: () -> Unit,
    onOpenClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
        ) {
            Row {
                Image(
                    painter = painterResource(id = appIconId),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                )
                Column (
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)

                ) {
                    Text(
                        text = appName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = metaInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = additonalInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Button(
                    onClick = onInstallClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text("Install", color = Color.Blue)
                }
                Button(
                    onClick = onOpenClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Open", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AppDetailScreen() {
    Column {
        AppInfoItem(
            appName = "GitHub",
            metaInfo = "GitHub, Inc.",
            additonalInfo = "In-app purchases",
            appIconId = com.example.myapplication.R.drawable.git_hub,
            onInstallClicked = {},
            onOpenClicked = {}
        )
    }
}

