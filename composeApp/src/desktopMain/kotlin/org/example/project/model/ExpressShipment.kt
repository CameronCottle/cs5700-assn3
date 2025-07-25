package org.example.project.model

class ExpressShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        val creation = getUpdateHistory().firstOrNull()?.timestamp ?: 0L
        if (newExpected > creation + 3 * 24 * 60 * 60 * 1000) {
            flagAbnormal("An express shipment has a delivery date more than 3 days after creation.")
        }
        super.delay(newExpected)
    }
}
