package org.example.project.observer

import org.example.project.model.Shipment

interface ShipmentUpdateListener {
    fun onShipmentUpdated(shipment: Shipment)
}
