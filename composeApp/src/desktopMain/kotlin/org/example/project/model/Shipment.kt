package org.example.project.model

import org.example.project.observer.ObservableShipment
import org.example.project.observer.ShipmentObserver

class Shipment(val id: String) : ObservableShipment {
    private var status: String = "created"
    private var currentLocation: String = "Unknown"
    private var expectedDeliveryDateTimestamp: Long = 0L
    private val notes = mutableListOf<String>()
    private val updateHistory = mutableListOf<ShippingUpdate>()

    fun getStatus() = status
    fun getLocation() = currentLocation
    fun getExpectedDeliveryDate() = expectedDeliveryDateTimestamp
    fun getNotes(): List<String> = notes.toList()
    fun getUpdateHistory(): List<ShippingUpdate> = updateHistory.toList()

    private val observers = mutableListOf<ShipmentObserver>()

    override fun addObserver(observer: ShipmentObserver) {
        observers.add(observer)
    }

    override fun removeObserver(observer: ShipmentObserver) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.onShipmentUpdated(this) }
    }

    fun updateStatus(newStatus: String, timestamp: Long) {
        println("updateStatus called with $newStatus at $timestamp (current = $status)")

        if (this.status == newStatus) {
            println("Skipping update: status already '$newStatus'")
            return
        }

        val update = ShippingUpdate(
            previousStatus = this.status,
            newStatus = newStatus,
            timestamp = timestamp
        )
        updateHistory.add(update)
        this.status = newStatus
        notifyObservers()
    }



    fun updateLocation(location: String) {
        currentLocation = location
        notifyObservers()
    }

    fun delay(newExpected: Long) {
        expectedDeliveryDateTimestamp = newExpected
        notifyObservers()
    }

    fun addNote(note: String) {
        if (note.isNotBlank()) {
            notes.add(note)
            notifyObservers()
        }
    }

}
