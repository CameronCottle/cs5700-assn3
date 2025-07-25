package org.example.project.model

import kotlin.compareTo

class OvernightShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        super.delay(newExpected)
    }

    override fun updateStatus(newStatus: String, timestamp: Long) {
        if (newStatus == "created") {
            super.updateStatus(newStatus, timestamp)
            // Initial expected delivery date = +1 day
            delay(timestamp + 24 * 60 * 60 * 1000)
        } else if (newStatus == "delayed") {
            super.updateStatus(newStatus, timestamp)
        } else if (getExpectedDeliveryDate() > getCreationTime() + 24 * 60 * 60 * 1000 &&
            newStatus == "delivered"
        ) {
            flagAbnormal("An overnight shipment was updated with a delivery date later than 24 hours after it was created.")
            super.updateStatus(newStatus, timestamp)
        } else {
            super.updateStatus(newStatus, timestamp)
        }
    }

    private fun getCreationTime(): Long {
        return getUpdateHistory().firstOrNull()?.timestamp ?: 0L
    }
}