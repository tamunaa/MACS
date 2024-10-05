import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*

@Composable
fun ScreensCarousel() {
    val imageIds = listOf(
        com.example.myapplication.R.drawable.a1,
        com.example.myapplication.R.drawable.a2,
        com.example.myapplication.R.drawable.a3,
        com.example.myapplication.R.drawable.a4,
        com.example.myapplication.R.drawable.a5,
        com.example.myapplication.R.drawable.a6,
        com.example.myapplication.R.drawable.a7,
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(imageIds) { imageId ->
            ImageCard(imageId)
        }
    }
}

@Composable
fun ImageCard(imageId: Int) {
    val image: Painter = painterResource(id = imageId)
    Image(
        painter = image,
        contentDescription = "App screenshot",
        modifier = Modifier
            .width(200.dp)
            .height(400.dp)
            .padding(4.dp)
    )
}
