package org.example.project.observer

import org.example.project.model.Shipment

// Anything that wants to be a listener and receive update must implement this interface (to receive tracking updates)
interface ShipmentObserver {
    fun onShipmentUpdated(shipment: Shipment)
}
