package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import assn2.composeapp.generated.resources.Res
import assn2.composeapp.generated.resources.compose_multiplatform
import org.example.project.navigation.ComposeNavigation
import org.example.project.navigation.Destinations
import org.example.project.screens.Tracker

@Composable
@Preview
fun App() {
    MaterialTheme {
       val navController = rememberNavController()
        val navigation = remember{ ComposeNavigation(navController) }

        NavHost(
            navController,
            startDestination = Destinations.Tracker
        ) {
            composable<Destinations.Tracker> {
                Home(navigation)
            }
        }
    }
}