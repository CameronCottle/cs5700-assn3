package org.example.project.model

class OvernightShipment(id: String) : Shipment(id) {

    fun shipped(expected: Long, timestamp: Long) {
        delay(expected)
        updateStatus("shipped", timestamp)

        val creationTime = getCreationTime()
        val oneDayLater = creationTime + (24 * 60 * 60 * 1000L)

        // Only flag abnormality if the shipment is being shipped on-time
        if (expected != oneDayLater && getStatus() != "delayed") {
            addNote("An overnight shipment was updated to include a delivery date not exactly 24 hours after creation.")
        }
    }
}

