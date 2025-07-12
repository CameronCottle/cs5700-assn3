package org.example.project.model

data class ShipmentUpdateRecord(
    val type: String,
    val shipmentId: String,
    val timestamp: Long,
    val extra: String? = null
)