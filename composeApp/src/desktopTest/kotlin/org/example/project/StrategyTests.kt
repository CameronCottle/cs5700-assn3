package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord
import kotlin.test.*

class StrategyTests {

    @Test
    fun `CreatedStrategy should update status and history`() {
        val shipment = Shipment("s1")
        val update = ShipmentUpdateRecord("created", "s1", 1000L, null)

        CreatedStrategy().applyUpdate(shipment, update)

        assertEquals("created", shipment.getStatus())
        assertEquals(1, shipment.getUpdateHistory().size)
        assertEquals("created", shipment.getUpdateHistory()[0].newStatus)
    }

    @Test
    fun `ShippedStrategy should update status and expectedDelivery`() {
        val shipment = Shipment("s2")
        val update = ShipmentUpdateRecord("shipped", "s2", 2000L, "3000")

        ShippedStrategy().applyUpdate(shipment, update)

        assertEquals("shipped", shipment.getStatus())
        assertEquals(3000L, shipment.getExpectedDeliveryDate())
        assertEquals(1, shipment.getUpdateHistory().size)
    }

    @Test
    fun `LocationStrategy should update location`() {
        val shipment = Shipment("s3")
        val update = ShipmentUpdateRecord("location", "s3", 2500L, "Denver CO")

        LocationStrategy().applyUpdate(shipment, update)

        assertEquals("Denver CO", shipment.getLocation())
        assertEquals(0, shipment.getUpdateHistory().size) // No status update should happen
    }


    @Test
    fun `DelayedStrategy should set new expected delivery date`() {
        val shipment = Shipment("s4")
        val update = ShipmentUpdateRecord("delayed", "s4", 3000L, "3500")

        DelayedStrategy().applyUpdate(shipment, update)

        assertEquals("delayed", shipment.getStatus())
        assertEquals(3500L, shipment.getExpectedDeliveryDate())
    }

    @Test
    fun `DeliveredStrategy should set status to delivered`() {
        val shipment = Shipment("s5")
        val update = ShipmentUpdateRecord("delivered", "s5", 4000L, null)

        DeliveredStrategy().applyUpdate(shipment, update)

        assertEquals("delivered", shipment.getStatus())
        assertEquals(1, shipment.getUpdateHistory().size)
    }

    @Test
    fun `CancelledStrategy should cancel shipment`() {
        val shipment = Shipment("s6")
        val update = ShipmentUpdateRecord("cancelled", "s6", 4500L, null)

        CancelledStrategy().applyUpdate(shipment, update)

        assertEquals("cancelled", shipment.getStatus())
    }

    @Test
    fun `LostStrategy should set status to lost`() {
        val shipment = Shipment("s7")
        val update = ShipmentUpdateRecord("lost", "s7", 4700L, null)

        LostStrategy().applyUpdate(shipment, update)

        assertEquals("lost", shipment.getStatus())
    }

    @Test
    fun `NoteAddedStrategy should add note`() {
        val shipment = Shipment("s8")
        val update = ShipmentUpdateRecord("noteadded", "s8", 5000L, "Box damaged")

        NoteAddedStrategy().applyUpdate(shipment, update)

        assertTrue(shipment.getNotes().contains("Box damaged"))
        assertEquals(1, shipment.getNotes().size)
    }

    // Edge case test
    @Test
    fun `ShippedStrategy should handle missing expectedDelivery gracefully`() {
        val shipment = Shipment("s9")
        val update = ShipmentUpdateRecord("shipped", "s9", 6000L, null)

        ShippedStrategy().applyUpdate(shipment, update)

        assertEquals("shipped", shipment.getStatus())
        assertEquals(0L, shipment.getExpectedDeliveryDate()) // Default
    }

    @Test
    fun `DelayedStrategy should ignore non-numeric extra`() {
        val shipment = Shipment("s10")
        val update = ShipmentUpdateRecord("delayed", "s10", 7000L, "not-a-number")

        DelayedStrategy().applyUpdate(shipment, update)

        assertEquals("delayed", shipment.getStatus())
        assertEquals(0L, shipment.getExpectedDeliveryDate())
    }
}
