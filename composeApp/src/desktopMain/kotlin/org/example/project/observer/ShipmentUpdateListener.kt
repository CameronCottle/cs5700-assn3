package org.example.project.observer

import org.example.project.model.Shipment

// Anything that wants to be a lisener and receive update must implement this interface (to reseive tracking updates)
interface ShipmentUpdateListener {
    fun onShipmentUpdated(shipment: Shipment)
}
