package org.example.project

import androidx.navigation.NavController

class ComposeNavigation(private val navController: NavController): Navigation {
    override fun goToSettings() {
        navController.navigate(Destinations.Settings)
    }

    override fun goBack() {
        navController.popBackStack()
    }
}