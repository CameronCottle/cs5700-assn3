package org.example.project.strategy

import org.example.project.model.BulkShipment
import org.example.project.model.ExpressShipment
import org.example.project.model.OvernightShipment
import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord
import org.example.project.model.StandardShipment

class ShippedStrategy : UpdateStrategy {
    override fun applyUpdate(shipment: Shipment, update: ShipmentUpdateRecord) {
        val expectedTimestamp = update.extra?.toLongOrNull()
        if (expectedTimestamp == null) return

        when (shipment) {
            is BulkShipment -> shipment.delay(expectedTimestamp)
            is OvernightShipment -> shipment.shipped(expectedTimestamp, update.timestamp)
            is ExpressShipment -> shipment.delay(expectedTimestamp)
            else -> shipment.delay(expectedTimestamp)
        }

        shipment.updateStatus("shipped", update.timestamp)
    }
}



