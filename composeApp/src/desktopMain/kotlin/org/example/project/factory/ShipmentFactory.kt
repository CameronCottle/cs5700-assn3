package org.example.project.factory

import org.example.project.model.BulkShipment
import org.example.project.model.ExpressShipment
import org.example.project.model.OvernightShipment
import org.example.project.model.Shipment
import org.example.project.model.StandardShipment

object ShipmentFactory {
    fun createShipment(id: String, type: String): Shipment {
        return when (type.lowercase()) {
            "bulk" -> BulkShipment(id)
            "express" -> ExpressShipment(id)
            "overnight" -> OvernightShipment(id)
            "standard" -> StandardShipment(id)
            else -> throw IllegalArgumentException("Unknown shipment type: $type")
        }
    }
}