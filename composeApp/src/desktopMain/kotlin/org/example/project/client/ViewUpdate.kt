package org.example.project.client

// class to type the information that needs to go to the UI
data class ViewUpdate(
    val id: String,
    val status: String,
    val location: String,
    val expectedDeliveryDate: String,
    val notes: List<String>,
    val updates: List<String>,
)