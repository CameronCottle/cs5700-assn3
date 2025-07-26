package org.example.project.factory

import org.example.project.model.Shipment

class ExpressShipment(id: String) : Shipment(id) {
    override fun delay(newExpected: Long) {
        super.delay(newExpected)
        val creationTime = getCreationTime()
        if (newExpected - creationTime > 3 * 24 * 60 * 60 * 1000L && getStatus() != "delayed") {
            addNote("Expected delivery exceeds 3-day limit for express shipment.")
        }
    }
}