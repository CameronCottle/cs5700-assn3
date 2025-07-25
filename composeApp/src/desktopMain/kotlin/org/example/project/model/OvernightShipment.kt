package org.example.project.model

class OvernightShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        super.delay(newExpected)

        val creationTime = getCreationTime()
        if (newExpected - creationTime > 24 * 60 * 60) {
            flagAbnormal("Overnight shipment has a delivery date more than 1 day after creation")
        }
    }
}
