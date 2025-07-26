package org.example.project.model

// class to type what should be in an update
data class ShipmentUpdateRecord(
    val type: String,
    val shipmentId: String,
    val timestamp: Long,
    val extra: String? = null
)