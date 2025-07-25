package org.example.project.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.navigation.ComposeNavigation
import org.example.project.client.TrackerViewHelper
import org.example.project.client.ViewUpdate

// This is the home (and only) page to this application
@Composable
fun Home(navigation: ComposeNavigation) {
    // to launch coroutines that are automatically cancelled when home is closed
    val coroutineScope = rememberCoroutineScope()
    // input from user
    var inputId by remember { mutableStateOf("") }
    // when a shipment ID is not valid
    var errorText by remember { mutableStateOf("") }

    // shipments being tracked
    val shipments = TrackerViewHelper.trackedShipments

    // start the simulation
//    LaunchedEffect(Unit) {
//        val file = File("test.txt")
//        TrackingServer.runSimulation(file)
//    }

    // I used AI to help me with the UI for two reasons:
    // 1. I am not familiar with building UI with Kotlin
    // 2. I am bad at making UI look good
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Track a Shipment", style = MaterialTheme.typography.headlineSmall)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = inputId,
                onValueChange = { inputId = it },
                label = { Text("Shipment ID") },
                modifier = Modifier.weight(1f)
            )

            Button(onClick = {
                coroutineScope.launch {
                    val success = TrackerViewHelper.trackShipment(inputId)
                    errorText = if (!success) {
                        println(errorText)
                        "Shipment $inputId does not exist or hasn't been created yet."
                    } else {
                        ""
                    }
                    inputId = ""
                }
            }) {
                Text("Track")
            }

            Button(onClick = {
                TrackerViewHelper.stopTracking(inputId)
                errorText = ""
                inputId = ""
            }) {
                Text("Stop")
            }
        }

        if (errorText.isNotEmpty()) {
            Text(errorText, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(shipments.values.toList(), key = { it.id }) { shipment ->
                ShipmentCard(shipment)
            }
        }
    }
}

// the card to be displayed for every shipment being tracked
@Composable
fun ShipmentCard(update: ViewUpdate) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Shipment ID: ${update.id}", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${update.status}")
            Text("Location: ${update.location}")
            Text("Expected Delivery: ${update.expectedDeliveryDate}")
            Text("Updates:")
            update.updates.forEach { updateText ->
                Text("• $updateText")
            }
            Text("Notes:")
            update.notes.forEach { note ->
                Text("• $note")
            }
        }
    }
}
