package org.example.project.view

data class ViewUpdate(
    val id: String,
    val status: String,
    val location: String,
    val expectedDeliveryDate: Long,
    val notes: List<String>,
    val updates: List<String>
)
