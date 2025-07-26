package org.example.project.strategy

import org.example.project.factory.OvernightShipment
import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord

class ShippedStrategy : UpdateStrategy {
    override fun applyUpdate(shipment: Shipment, update: ShipmentUpdateRecord) {
        val expectedTimestamp = update.extra?.toLongOrNull() ?: 0L

        when (shipment) {
            is OvernightShipment -> shipment.shipped(expectedTimestamp, update.timestamp)
            else -> shipment.delay(expectedTimestamp)
        }

        shipment.updateStatus("shipped", update.timestamp)
    }
}




