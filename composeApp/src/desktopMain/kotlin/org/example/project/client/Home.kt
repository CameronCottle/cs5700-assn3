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
    val coroutineScope = rememberCoroutineScope()
    var inputId by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    // shipments being tracked
    val shipments = TrackerViewHelper.trackedShipments

    // Automatically clear error text after 7 seconds
    LaunchedEffect(errorText) {
        if (errorText.isNotEmpty()) {
            kotlinx.coroutines.delay(7000)
            errorText = ""
        }
    }

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
                    if (TrackerViewHelper.trackedShipments.containsKey(inputId)) {
                        TrackerViewHelper.trackedOrder.remove(inputId)
                        TrackerViewHelper.trackedOrder.add(0, inputId)
                        errorText = "You are already tracking Shipment $inputId."
                    } else {
                        val success = TrackerViewHelper.trackShipment(inputId)
                        errorText = if (!success) {
                            "Shipment $inputId does not exist or hasn't been created yet."
                        } else {
                            ""
                        }
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
            items(TrackerViewHelper.trackedOrder, key = { it }) { id ->
                TrackerViewHelper.trackedShipments[id]?.let { ShipmentCard(it) }
            }
        }
    }
}

// the card to be displayed for every shipment being tracked
@Composable
fun ShipmentCard(update: ViewUpdate) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Shipment ID: ${update.id}", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${update.status}")
            Text("Location: ${update.location}")
            Text("Expected Delivery: ${update.expectedDeliveryDate}")

            if (update.isAbnormal && update.abnormalMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Warning: ${update.abnormalMessage}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Updates:")
            update.updates.forEach { updateText ->
                Text("• $updateText")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Notes:")
            update.notes.forEach { note ->
                Text("• $note")
            }
        }
    }
}

