package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord

class ShippedStrategy : UpdateStrategy {
    override fun applyUpdate(shipment: Shipment, update: ShipmentUpdateRecord) {
        val newExpectedDate = update.extra?.toLongOrNull()
        if (newExpectedDate != null) {
            shipment.delay(newExpectedDate)
        }
        shipment.updateStatus("shipped", update.timestamp)
    }
}
