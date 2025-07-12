package org.example.project.simulator

import kotlinx.coroutines.delay
import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord
import org.example.project.strategy.CreatedStrategy
import java.io.File

object TrackingSimulator {
    private val shipments = mutableMapOf<String, Shipment>()

    private val strategies = mapOf(
        "created" to CreatedStrategy()
    )

    suspend fun runSimulation(file: File) {
        for (line in file.readLines()) {
            val update = parseLine(line)
            println("Processing line: $line")
            val shipment = shipments.getOrPut(update.shipmentId) {
                Shipment(update.shipmentId)
            }
            strategies[update.type]?.apply(shipment, update)
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
}
