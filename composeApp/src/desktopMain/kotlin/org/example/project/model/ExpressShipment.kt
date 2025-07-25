package org.example.project.model

class ExpressShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        super.delay(newExpected)
        val creationTime = getCreationTime()
        if (newExpected - creationTime > 3 * 24 * 60 * 60 * 1000L) {
            addNote("Expected delivery exceeds 3-day limit for express shipment.")
        }
    }
}

