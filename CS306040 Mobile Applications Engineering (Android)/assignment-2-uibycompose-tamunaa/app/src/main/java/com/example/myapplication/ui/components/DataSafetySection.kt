import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DataSafetySection() {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Text(
                "Data Safety",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.ArrowDropDown,
                contentDescription = "Toggle Details",
                modifier = Modifier.graphicsLayer(rotationZ = if (expanded) 180f else 0f)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.contentColorFor(Color.Blue),
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Safety starts with understanding how developers collect and share your data. Data privacy and security practices may vary based on your use, region, and age. The developers provided this information and may update over time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DataPointRow(Icons.Filled.Lock, "No data shared with third parties", "Learn more about how developers handle your data")
                    Spacer(modifier = Modifier.height(8.dp))
                    DataPointRow(Icons.Filled.Warning, "This app may collect these data types", "App activity, App info and performance, and device or other IDs")
                    Spacer(modifier = Modifier.height(8.dp))
                    DataPointRow(icon = Icons.Filled.Lock, text = "Data is encrypted in transit")
                    DataPointRow(Icons.Filled.Delete, "Data can't be deleted")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "See details",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .clickable {}
                            .padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun DataPointRow(icon: ImageVector, text: String, subText: String? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text, style = MaterialTheme.typography.bodyMedium)
            subText?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
