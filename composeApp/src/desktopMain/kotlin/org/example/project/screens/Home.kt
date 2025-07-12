package org.example.project.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.example.project.simulator.TrackingSimulator
import java.io.File
import androidx.compose.material3.Text
import org.example.project.navigation.ComposeNavigation

@Composable
fun Home(navigation: ComposeNavigation) {
    LaunchedEffect(Unit) {
        val file = File("test.txt")
        TrackingSimulator.runSimulation(file)
    }
    Text("Shipment simulation is running...")
}
