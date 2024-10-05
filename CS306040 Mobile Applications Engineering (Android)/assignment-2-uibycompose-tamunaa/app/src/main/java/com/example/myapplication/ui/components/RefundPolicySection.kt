package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RefundPolicySection() {
    Row(

        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
        Text(
            "Refund Policy",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

    }

    Text(text = "All prices include VAT.", modifier = Modifier.padding(16.dp))
}



