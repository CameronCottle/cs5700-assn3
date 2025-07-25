package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord

class CreatedStrategy : UpdateStrategy {
    override fun applyUpdate(shipment: Shipment, update: ShipmentUpdateRecord) {
        if (shipment.getStatus() != "created") {
            shipment.updateStatus("created", update.timestamp)
        }
    }
}
