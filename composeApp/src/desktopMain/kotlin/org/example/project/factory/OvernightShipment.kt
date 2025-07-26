package org.example.project.factory

import org.example.project.model.Shipment

class OvernightShipment(id: String) : Shipment(id) {

    fun shipped(expected: Long, timestamp: Long) {
        delay(expected)
        updateStatus("shipped", timestamp)

        val creationTime = getCreationTime()
        val oneDayLater = creationTime + (24 * 60 * 60 * 1000L)

        if (expected != oneDayLater && getStatus() != "delayed") {
            addNote("An overnight shipment was updated to include a delivery date not exactly 24 hours after creation.")
        }
    }
}