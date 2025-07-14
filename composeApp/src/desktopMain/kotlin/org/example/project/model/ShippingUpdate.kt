package org.example.project.model

// class that defines a shipping update, only business logic
data class ShippingUpdate(
    val previousStatus: String,
    val newStatus: String,
    val timestamp: Long
)
