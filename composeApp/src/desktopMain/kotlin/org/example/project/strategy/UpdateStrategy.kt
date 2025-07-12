package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord

interface UpdateStrategy {
    fun apply(shipment: Shipment, update: ShipmentUpdateRecord)
}