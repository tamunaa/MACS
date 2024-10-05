package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AboutAppSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "About this app",
            style = MaterialTheme.typography.h6,
        )
        Text(
            text = "Triage notifications, review, comment, and merge, right from your mobile device.",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}