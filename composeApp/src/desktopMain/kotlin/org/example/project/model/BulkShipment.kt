package org.example.project.model

class BulkShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        val createdAt = getCreationTime()
        val threeDaysMillis = 3 * 24 * 60 * 60 * 1000L

        if (newExpected < createdAt + threeDaysMillis) {
            addNote("Shipment is expected sooner than the 3-day requirement for bulk shipment.")
        }

        super.delay(newExpected)
    }
}

