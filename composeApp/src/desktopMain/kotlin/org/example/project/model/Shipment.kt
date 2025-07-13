package org.example.project.model

import org.example.project.model.ShippingUpdate
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

}
