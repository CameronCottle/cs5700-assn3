package org.example.project.observer

interface ShipmentNotifier {
    fun addObserver(observer: ShipmentUpdateListener)
    fun removeObserver(observer: ShipmentUpdateListener)
    fun notifyObservers()
}
