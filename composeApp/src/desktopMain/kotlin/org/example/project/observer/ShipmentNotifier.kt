package org.example.project.observer

// Anything that wants to be an observable must implement this interface
interface ShipmentNotifier {
    fun addObserver(observer: ShipmentUpdateListener)
    fun removeObserver(observer: ShipmentUpdateListener)
    fun notifyObservers()
}
