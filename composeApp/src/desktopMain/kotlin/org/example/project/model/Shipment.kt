package org.example.project.model

import org.example.project.model.ShippingUpdate

class Shipment(val id: String) {
    private var status: String = "created"
    private var currentLocation: String = "Unknown"
    private var expectedDeliveryDateTimestamp: Long = 0L
    private val notes = mutableListOf<String>()
    private val updateHistory = mutableListOf<ShippingUpdate>()

    fun updateStatus(newStatus: String, timestamp: Long) {
        updateHistory.add(ShippingUpdate(status, newStatus, timestamp))
        status = newStatus
    }

    fun updateLocation(location: String) {
        currentLocation = location
    }

    fun delay(newExpected: Long) {
        expectedDeliveryDateTimestamp = newExpected
    }

    fun addNote(note: String) {
        notes.add(note)
    }

    // For testing output
    fun debugPrint() {
        println("Shipment $id: status=$status, location=$currentLocation, expected=$expectedDeliveryDateTimestamp")
        println("Notes: $notes")
        println("Updates:")
        updateHistory.forEach {
            println("  ${it.previousStatus} -> ${it.newStatus} at ${it.timestamp}")
        }
    }
}
