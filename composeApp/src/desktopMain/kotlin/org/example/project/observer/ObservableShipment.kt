package org.example.project.observer

interface ObservableShipment {
    fun addObserver(observer: ShipmentObserver)
    fun removeObserver(observer: ShipmentObserver)
    fun notifyObservers()
}
