import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

data class App(
    val name: String,
    val description: String,
    val iconUrl: Int
)

@Composable
fun AppCard(app: App) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = app.iconUrl),
                contentDescription = "${app.name} app icon",
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = app.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = app.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MoreAppsToTrySection() {
    val sampleApps = listOf(
        App("Enki", "Master new skills in programming with daily workouts.", R.drawable.anki),
        App("Medium", "Explore thought-provoking content from world-renowned writers.", R.drawable.medium),
        App("Ted", "Discover a world of ideas with TED Talks videos and audios.", R.drawable.ted),
        App("Termius", "Manage your remote projects with this secure SSH client.", R.drawable.termius),
        App("Proto", "Prototype faster with real-time collaboration tools.", R.drawable.proto)
    )

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Similar Apps",
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = "More apps",
                modifier = Modifier.clickable {}
            )
        }
        LazyRow(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(sampleApps) { app ->
                AppCard(app)
            }
        }
    }
}
