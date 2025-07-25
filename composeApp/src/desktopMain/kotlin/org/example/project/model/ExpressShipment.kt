package org.example.project.model

class ExpressShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        super.delay(newExpected)

        val creation = getCreationTime()
        if (newExpected - creation > 3 * 24 * 60 * 60) {
            flagAbnormal("Express shipment delivery is more than 3 days after creation.")
        }
    }

}
