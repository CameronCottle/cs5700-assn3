package org.example.project.model

class OvernightShipment(id: String) : Shipment(id) {
    fun shipped(expected: Long, timestamp: Long) {
        delay(expected)
        updateStatus("shipped", timestamp)

        val creationTime = getCreationTime()
        val oneDayLater = creationTime + (24 * 60 * 60 * 1000L)
        if (expected != oneDayLater) {
            addNote("Overnight shipment expected delivery is not exactly 1 day after creation.")
        }
    }
}

