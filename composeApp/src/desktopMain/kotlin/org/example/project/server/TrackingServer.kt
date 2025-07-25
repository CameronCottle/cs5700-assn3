package org.example.project.server

import org.example.project.factory.ShipmentFactory
import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord
import org.example.project.model.StandardShipment
import org.example.project.strategy.CancelledStrategy
import org.example.project.strategy.CreatedStrategy
import org.example.project.strategy.DelayedStrategy
import org.example.project.strategy.DeliveredStrategy
import org.example.project.strategy.LocationStrategy
import org.example.project.strategy.LostStrategy
import org.example.project.strategy.NoteAddedStrategy
import org.example.project.strategy.ShippedStrategy
import org.example.project.strategy.UpdateStrategy


object TrackingServer {
    private val shipments = mutableMapOf<String, Shipment>()

    private val strategies: Map<String, UpdateStrategy> = mapOf(
        "created" to CreatedStrategy(),
        "shipped" to ShippedStrategy(),
        "location" to LocationStrategy(),
        "cancelled" to CancelledStrategy(),
        "lost" to LostStrategy(),
        "noteadded" to NoteAddedStrategy(),
        "delayed" to DelayedStrategy(),
        "delivered" to DeliveredStrategy()
    )

    private fun parseLine(line: String): ShipmentUpdateRecord {
        println("Parsing line: $line")
        val parts = line.split(",", limit = 4)

        val type = parts[0].trim().lowercase()
        val shipmentId = parts[1].trim()

        return when (type) {
            "created" -> {
                val shipmentType = parts[2].trim()
                val timestamp = parts[3].trim().toLong()
                ShipmentUpdateRecord(
                    type = type,
                    shipmentId = shipmentId,
                    timestamp = timestamp,
                    extra = shipmentType
                )
            }
            else -> {
                val timestamp = parts[2].trim().toLong()
                val extra = parts.getOrNull(3)?.trim()
                ShipmentUpdateRecord(
                    type = type,
                    shipmentId = shipmentId,
                    timestamp = timestamp,
                    extra = extra
                )
            }
        }
    }

    // public so UI can inspect current state
    fun findShipment(id: String): Shipment? = shipments[id.trim()]

    fun applyUpdateFromString(updateString: String): Boolean {
        val update = parseLine(updateString)

        val shipment = shipments.getOrPut(update.shipmentId) {
            if (update.type == "created") {
                val shipmentType = update.extra ?: throw IllegalArgumentException("Missing shipment type for 'created'")
                ShipmentFactory.createShipment(update.shipmentId, shipmentType)

            } else {
                StandardShipment(update.shipmentId)
            }
        }

        return strategies[update.type]?.applyUpdate(shipment, update) != null
    }



    // for unit test injection
    fun registerShipment(id: String, shipment: Shipment) {
        shipments[id] = shipment
    }
}