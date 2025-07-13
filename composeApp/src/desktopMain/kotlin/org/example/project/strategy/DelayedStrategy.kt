package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord

class DelayedStrategy : UpdateStrategy {
    override fun applyUpdate(shipment: Shipment, update: ShipmentUpdateRecord) {
        val newExpected = update.extra?.toLongOrNull() ?: 0L
        shipment.delay(newExpected)
        shipment.updateStatus("delayed", update.timestamp)
    }
}
