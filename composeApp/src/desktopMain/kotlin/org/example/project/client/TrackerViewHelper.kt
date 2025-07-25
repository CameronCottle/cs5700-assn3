package org.example.project.client

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.model.Shipment
import org.example.project.observer.ShipmentUpdateListener
import org.example.project.server.TrackingServer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TrackerViewHelper : ShipmentUpdateListener {

    val trackedShipments = mutableStateMapOf<String, ViewUpdate>()
    val trackedOrder = mutableStateListOf<String>()
    private val activeTrackIds = mutableSetOf<String>()

    override fun onShipmentUpdated(shipment: Shipment) {
        if (shipment.getId() in activeTrackIds) {
            trackedShipments[shipment.getId()] = convertToViewUpdate(shipment)
        }
    }

    suspend fun trackShipment(id: String): Boolean = withContext(Dispatchers.IO) {
        val shipment = TrackingServer.findShipment(id)
        return@withContext if (shipment != null) {
            val isNew = activeTrackIds.add(id)
            shipment.addObserver(this@TrackerViewHelper)
            trackedShipments[id] = convertToViewUpdate(shipment)

            synchronized(trackedOrder) {
                trackedOrder.remove(id)
                trackedOrder.add(0, id)
            }

            true
        } else {
            false
        }
    }

    fun stopTracking(id: String) {
        val shipment = TrackingServer.findShipment(id)
        shipment?.removeObserver(this)
        activeTrackIds.remove(id)
        trackedShipments.remove(id)
        trackedOrder.remove(id)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun convertToViewUpdate(shipment: Shipment): ViewUpdate {
        return ViewUpdate(
            id = shipment.getId(),
            status = shipment.getStatus(),
            location = shipment.getLocation(),
            expectedDeliveryDate = if (shipment.getExpectedDeliveryDate() != 0L)
                formatTimestamp(shipment.getExpectedDeliveryDate())
            else
                "--",
            notes = shipment.getNotes(),
            updates = shipment.getUpdateHistory().map {
                "Shipment went from ${it.previousStatus} to ${it.newStatus} on ${formatTimestamp(it.timestamp)}"
            }
        )
    }
}
