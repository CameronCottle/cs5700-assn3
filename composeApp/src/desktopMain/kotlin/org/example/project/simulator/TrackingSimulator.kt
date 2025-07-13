package org.example.project.simulator

import kotlinx.coroutines.delay
import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord
import org.example.project.observer.ConsoleLoggerObserver
import org.example.project.strategy.CancelledStrategy
import org.example.project.strategy.CreatedStrategy
import org.example.project.strategy.DelayedStrategy
import org.example.project.strategy.DeliveredStrategy
import org.example.project.strategy.LocationStrategy
import org.example.project.strategy.LostStrategy
import org.example.project.strategy.NoteAddedStrategy
import org.example.project.strategy.ShippedStrategy
import org.example.project.strategy.UpdateStrategy
import java.io.File

object TrackingSimulator {
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

    suspend fun runSimulation(file: File) {
        for (line in file.readLines()) {
            val update = parseLine(line)
            val shipment = shipments.getOrPut(update.shipmentId) {
                Shipment(update.shipmentId)
            }
            strategies[update.type]?.applyUpdate(shipment, update)
            delay(1000L)
        }
    }


    private fun parseLine(line: String): ShipmentUpdateRecord {
        val parts = line.split(",", limit = 4)
        return ShipmentUpdateRecord(
            type = parts[0],
            shipmentId = parts[1],
            timestamp = parts[2].toLong(),
            extra = parts.getOrNull(3)
        )
    }

    fun getShipment(id: String): Shipment? {
        val cleanId = id.trim()
        println("Looking for shipment ID: '$cleanId'")
        println("Available keys: ${shipments.keys}")
        return shipments[cleanId]
    }


}
