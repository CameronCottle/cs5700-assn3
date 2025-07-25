package org.example.project.model

class BulkShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        super.delay(newExpected)
        val creationTime = getCreationTime()
        if (newExpected - creationTime < 3 * 24 * 60 * 60 * 1000L) {
            addNote("Expected delivery is earlier than 3-day minimum for bulk shipment.")
        }
    }

    fun shipped(expected: Long, timestamp: Long) {
        delay(expected)  // Reuse existing logic
        updateStatus("shipped", timestamp)
    }

}

