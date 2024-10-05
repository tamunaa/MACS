import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import com.example.myapplication.R

data class SupportInfo(
    val icon: Int,
    val info: String,
    val description: String?
)

@Composable
fun AppSupportSection() {
    var expanded by remember { mutableStateOf(false) }
    val supportInfoList = listOf(
        SupportInfo(R.drawable.web, "Website", null),
        SupportInfo(R.drawable.email, "Email", "ttsot21@freeuni.edu.ge"),
        SupportInfo(R.drawable.address, "Address", "1234 Street, City"),
        SupportInfo(R.drawable.privacy, "Privacy Policy", null)
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("App Support", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                supportInfoList.forEach { supportInfo ->
                    SupportItem(supportInfo)
//                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                }
            }
        }
    }
}

@Composable
fun SupportItem(supportInfo: SupportInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = supportInfo.icon),
            contentDescription = supportInfo.info,
            modifier = Modifier.size(24.dp).padding(end = 8.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(supportInfo.info, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
            supportInfo.description?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
