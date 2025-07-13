package org.example.project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.navigation.ComposeNavigation
import org.example.project.view.TrackerViewHelper
import org.example.project.view.ViewUpdate
import org.example.project.simulator.TrackingSimulator
import java.io.File


@Composable
fun Home(navigation: ComposeNavigation) {
    val coroutineScope = rememberCoroutineScope()
    var inputId by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val shipments = TrackerViewHelper.trackedShipments

    LaunchedEffect(Unit) {
        val file = File("test.txt")
        if (file.exists()) {
            println("Starting simulation from test.txt...")
            TrackingSimulator.runSimulation(file)
        } else {
            println("ERROR: test.txt file not found.")
        }
    }


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
            if (update.notes.isNotEmpty()) {
                Text("Notes:")
                update.notes.forEach { note ->
                    Text("- $note")
                }
            }
            if (update.updates.isNotEmpty()) {
                Text("Updates:")
                update.updates.forEach { updateText ->
                    Text("• $updateText")
                }
            }
            Text("Notes:")
            update.notes.forEach { note ->
                Text("• $note")
            }
        }
    }
}
