package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord

class LocationStrategy : UpdateStrategy {
    override fun applyUpdate(shipment: Shipment, update: ShipmentUpdateRecord) {
        val newLocation = update.extra
        if (!newLocation.isNullOrBlank()) {
            shipment.updateLocation(newLocation)
        }
    }
}
