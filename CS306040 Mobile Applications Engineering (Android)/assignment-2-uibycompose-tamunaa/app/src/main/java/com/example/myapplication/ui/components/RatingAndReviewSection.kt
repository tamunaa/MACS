import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Text
data class RatingDistribution(
    val fiveStar: Int,
    val fourStar: Int,
    val threeStar: Int,
    val twoStar: Int,
    val oneStar: Int
)

data class AppReviews(
    val averageRating: Double,
    val totalRatings: Int,
    val distribution: RatingDistribution
)


@Composable
fun RatingsAndReviewsSection() {
    val appReviews = AppReviews(
        averageRating = 4.5,
        totalRatings = 100,
        distribution = RatingDistribution(
            fiveStar = 80,
            fourStar = 10,
            threeStar = 5,
            twoStar = 3,
            oneStar = 2
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Ratings & Reviews",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Ratings and reviews are verified and are from people who used the same type of device as you.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            OverallRatingDisplay(appReviews = appReviews, modifier = Modifier.weight(1f))
            RatingsDistribution(appReviews.distribution, appReviews.totalRatings, modifier = Modifier.weight(2f))
        }
    }
}

@Composable
fun OverallRatingDisplay(appReviews: AppReviews, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "${appReviews.averageRating}",
            style = MaterialTheme.typography.headlineLarge,
        )
        RatingBar(rating = appReviews.averageRating)
    }
}

@Composable
fun RatingsDistribution(distribution: RatingDistribution, totalRatings: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        listOf(
            "5" to distribution.fiveStar,
            "4" to distribution.fourStar,
            "3" to distribution.threeStar,
            "2" to distribution.twoStar,
            "1" to distribution.oneStar
        ).forEach { (label, count) ->
            RatingDistributionRow(label, count, totalRatings)
        }
    }
}

@Composable
fun RatingDistributionRow(label: String, count: Int, total: Int) {
    val ratio = if (total > 0) count.toFloat() / total else 0f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = ratio,
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = MaterialTheme.colorScheme.primary,
            backgroundColor = Color.LightGray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count",
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun RatingBar(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "$i star rating",
                tint = if (i <= rating) Color.Blue else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
