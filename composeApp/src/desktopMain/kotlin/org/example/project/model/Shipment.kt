package org.example.project.model

import org.example.project.observer.ShipmentNotifier
import org.example.project.observer.ShipmentUpdateListener

class Shipment(private val id: String) : ShipmentNotifier {
    // initialize as created and unknown because it should always be status created first, and it doesn't have a location before it is shipped
    private var status: String = "created"
    private var currentLocation: String = "Unknown"
    private var expectedDeliveryDateTimestamp: Long = 0L
    private val notes = mutableListOf<String>()
    private val updateHistory = mutableListOf<ShippingUpdate>()

    // public getter functions
    fun getId() = id
    fun getStatus() = status
    fun getLocation() = currentLocation
    fun getExpectedDeliveryDate() = expectedDeliveryDateTimestamp
    fun getNotes(): List<String> = notes.toList()
    fun getUpdateHistory(): List<ShippingUpdate> = updateHistory.toList()

    // list of observers to send notifications to
    private val observers = mutableListOf<ShipmentUpdateListener>()

    override fun addObserver(observer: ShipmentUpdateListener) {
        observers.add(observer)
    }

    override fun removeObserver(observer: ShipmentUpdateListener) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.onShipmentUpdated(this) }
    }

    // below are the methods to update the information about a shipment. It is important to notify the observers after an update
    fun updateStatus(newStatus: String, timestamp: Long) {

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
