package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord

interface UpdateStrategy {
    fun applyUpdate(shipment: Shipment, update: ShipmentUpdateRecord)
}