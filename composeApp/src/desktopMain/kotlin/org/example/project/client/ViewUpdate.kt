package org.example.project.client

// Complete information of a shipment
data class ViewUpdate(
    val id: String,
    val status: String,
    val location: String,
    val expectedDeliveryDate: String,
    val notes: List<String>,
    val updates: List<String>
)