import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.example.myapplication.R

@Composable
fun ReviewItem(review: Review) {
    var showMenu by remember { mutableStateOf(false) }
    var menuPosition by remember { mutableStateOf(IntOffset(0, 0)) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id= review.photo),
                    contentDescription = "User Photo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${review.name} ${review.surname}", fontWeight = FontWeight.Bold)
            }

            Box {
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        menuPosition = IntOffset(coordinates.positionInRoot().x.toInt(), coordinates.boundsInRoot().bottom.toInt())
                    }
                ) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.width(IntrinsicSize.Min)
                ) {
                    DropdownMenuItem(onClick = {
                        showMenu = false
                    }) {
                        Text("Flag as Inappropriate")
                    }
                    DropdownMenuItem(onClick = {
                        showMenu = false
                    }) {
                        Text("Flag as Spam")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        RatingBar(review.rating.toDouble())
        Text(review.review, style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Was this review helpful?")
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yes",
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .clickable {}
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "No",
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .clickable {}
                )
            }
        }
    }
}
data class Review(
    val id: String,
    val photo: Int ,
    val name: String,
    val surname: String,
    val rating: Int,
    val review: String,
    val helpful: Int
)

@Composable
fun ReviewsList() {
    val reviews = listOf(
        Review(
            id = "1",
            photo = R.drawable.usr1,
            name = "John",
            surname = "Doe",
            rating = 5,
            review = "Great app!",
            helpful = 10
        ),
        Review(
            id = "2",
            photo = R.drawable.usr2,
            name = "Jane",
            surname = "Doe",
            rating = 2,
            review = "Good app, but needs some improvements.",
            helpful = 8
        ),
        Review(
            id = "3",
            photo = R.drawable.usr3,
            name = "Alice",
            surname = "Johnson",
            rating = 4,
            review = "Really useful, but could be more user-friendly.",
            helpful = 7
        ),
        Review(
            id = "4",
            photo = R.drawable.usr4,
            name = "Bob",
            surname = "Smith",
            rating = 3,
            review = "Decent functionality, but has some bugs.",
            helpful = 5
        )
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        for (review in reviews) {
            ReviewItem(review)
        }
        Text(text = "See all reviews", color = Color.Blue, modifier = Modifier.padding(16.dp).clickable {})
    }
}
