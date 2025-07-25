package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview

import org.example.project.navigation.ComposeNavigation
import org.example.project.navigation.Destinations
import org.example.project.client.Home

@Composable
@Preview
fun App() {
    MaterialTheme {
       val navController = rememberNavController()
        val navigation = remember{ ComposeNavigation(navController) }

        NavHost(
            navController,
            startDestination = Destinations.Home
        ) {
            composable<Destinations.Home> {
                Home(navigation)
            }
        }
    }
}