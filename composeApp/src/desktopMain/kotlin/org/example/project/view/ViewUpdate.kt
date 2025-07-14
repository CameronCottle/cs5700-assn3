package org.example.project.view

// Complete information of a shipment
data class ViewUpdate(
    val id: String,
    val status: String,
    val location: String,
    val expectedDeliveryDate: String,
    val notes: List<String>,
    val updates: List<String>
)
