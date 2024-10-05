package ge.edu.freeuni.forum.ui.component.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ge.edu.freeuni.forum.R

@Composable
fun CompactCreatePost(onExpand: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(onClick = onExpand),
            elevation = 8.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary.copy(alpha = 0.05f),
                                MaterialTheme.colors.primary.copy(alpha = 0.02f)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_thinking),
                        contentDescription = "Icon",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "What do you want to ask or share?",
                        style = MaterialTheme.typography.subtitle1.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.think),
            contentDescription = "Centered Image",
            modifier = Modifier
                .padding(100.dp)
                .size(200.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}
