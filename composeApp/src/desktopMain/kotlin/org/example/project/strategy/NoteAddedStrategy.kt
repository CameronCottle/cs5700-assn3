package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord

class NoteAddedStrategy : UpdateStrategy {
    override fun applyUpdate(shipment: Shipment, update: ShipmentUpdateRecord) {
        val note = update.extra
        if (!note.isNullOrBlank()) {
            shipment.addNote(note)
        }
    }
}

