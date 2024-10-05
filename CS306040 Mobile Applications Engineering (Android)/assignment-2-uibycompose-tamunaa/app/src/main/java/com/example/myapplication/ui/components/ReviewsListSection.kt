package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ReviewsListSection() {
    Column {
        Text("User Reviews", fontWeight = FontWeight.Bold)
        for (i in 1..3) {
            Text("User $i: Really useful app!")
        }
    }
}
