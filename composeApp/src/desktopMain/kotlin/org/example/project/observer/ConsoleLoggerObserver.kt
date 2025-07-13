package org.example.project.observer

import org.example.project.model.Shipment

class ConsoleLoggerObserver : ShipmentObserver {
    override fun onShipmentUpdated(shipment: Shipment) {
        println("[Observer] Shipment ${shipment.id} updated: status=${shipment.getStatus()}, location=${shipment.getLocation()}")
    }
}
