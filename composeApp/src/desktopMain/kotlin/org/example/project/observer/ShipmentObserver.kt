package org.example.project.observer

import org.example.project.model.Shipment

interface ShipmentObserver {
    fun onShipmentUpdated(shipment: Shipment)
}
