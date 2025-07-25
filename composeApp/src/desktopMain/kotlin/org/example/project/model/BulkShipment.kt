package org.example.project.model

class BulkShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        val creation = getCreationTime()
        if (newExpected < creation + 3 * 24 * 60 * 60 * 1000) {
            flagAbnormal("A bulk shipment was updated with a delivery date too soon (less than 3 days).")
        }
        super.delay(newExpected)
    }

    private fun getCreationTime(): Long {
        return getUpdateHistory().firstOrNull()?.timestamp ?: 0L
    }
}

