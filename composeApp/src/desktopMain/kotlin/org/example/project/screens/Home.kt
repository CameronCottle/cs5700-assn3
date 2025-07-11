package org.example.project.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.example.project.navigation.Destinations
import org.example.project.navigation.Navigation

@Composable
fun Home (navigation: Navigation) {

    Column {
        Text("Home Page")
        Button(onClick = {
            navigation.goToSettings()
        }) {
            Text("Go to Settings")
        }
    }
}