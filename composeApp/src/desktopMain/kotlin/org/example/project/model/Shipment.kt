package org.example.project.model

class Shipment(private val id: String) {

    private var status = ""
    private val notes = mutableListOf<String>()
    private val updateHistory = mutableListOf<ShippingUpdate>()
    private var expectedDeliveryDateTimestamp: Long = 0
    private var currentLocation: String = ""

    fun getId(): String = id
    fun getStatus(): String = status
    fun getUpdateHistory(): List<ShippingUpdate> = updateHistory
    fun getNotes(): List<String> = notes

    fun updateStatus(newStatus: String, timestamp: Long) {
        val previous = status
        status = newStatus
        updateHistory.add(ShippingUpdate(previous, newStatus, timestamp))
        println("Shipment $id went from $previous to $newStatus at $timestamp")
        // notifyObservers() — add later
    }
}