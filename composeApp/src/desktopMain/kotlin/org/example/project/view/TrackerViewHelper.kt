package org.example.project.view

import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.model.Shipment
import org.example.project.observer.ShipmentObserver
import org.example.project.simulator.TrackingSimulator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TrackerViewHelper : ShipmentObserver {

    // Public observable map for Compose UI
    val trackedShipments = mutableStateMapOf<String, ViewUpdate>()

    // Internal record of which shipments are being tracked
    private val activeTrackIds = mutableSetOf<String>()

    override fun onShipmentUpdated(shipment: Shipment) {
        if (shipment.id in activeTrackIds) {
            trackedShipments[shipment.id] = convertToViewUpdate(shipment)
        }
    }

    suspend fun trackShipment(id: String): Boolean = withContext(Dispatchers.IO) {
        val shipment = TrackingSimulator.getShipment(id)
        println(shipment)
        return@withContext if (shipment != null) {
            shipment.addObserver(this@TrackerViewHelper)
            activeTrackIds.add(id)
            println(activeTrackIds)
            trackedShipments[id] = convertToViewUpdate(shipment)
            true
        } else {
            false
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun stopTracking(id: String) {
        val shipment = TrackingSimulator.getShipment(id)
        shipment?.removeObserver(this)
        activeTrackIds.remove(id)
        trackedShipments.remove(id)
    }

    private fun convertToViewUpdate(shipment: Shipment): ViewUpdate {
        return ViewUpdate(
            id = shipment.id,
            status = shipment.getStatus(),
            location = shipment.getLocation(),
            expectedDeliveryDate = formatTimestamp(shipment.getExpectedDeliveryDate()),
            notes = shipment.getNotes(),
            updates = shipment.getUpdateHistory().map {
                "Shipment went from ${it.previousStatus} to ${it.newStatus} on ${formatTimestamp(it.timestamp)}"
            }
        )
    }
}
