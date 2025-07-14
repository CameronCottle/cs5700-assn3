package org.example.project.model

// interface for anything that wants to be an observable
data class ShipmentUpdateRecord(
    val type: String,
    val shipmentId: String,
    val timestamp: Long,
    val extra: String? = null
)