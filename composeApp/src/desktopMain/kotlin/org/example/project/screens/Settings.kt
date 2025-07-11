package org.example.project.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Settings(
    goBack: () -> Unit
) {
    Column {
        Text("Settings Page")
        Button(onClick = {
            goBack()
        }) {
            Text("Back")
        }
    }
}