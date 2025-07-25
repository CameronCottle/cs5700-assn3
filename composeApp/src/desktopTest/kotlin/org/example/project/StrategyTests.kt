package org.example.project.strategy

import org.example.project.model.*
import kotlin.test.*

class StrategyTests {

    @Test
    fun `CreatedStrategy should not create duplicate update if status is already created`() {
        val shipment = StandardShipment("s1")
        val update = ShipmentUpdateRecord("created", "s1", 1000L, null)

        CreatedStrategy().applyUpdate(shipment, update)

        assertEquals("created", shipment.getStatus())
        assertEquals(0, shipment.getUpdateHistory().size) // nothing added
    }

    @Test
    fun `ShippedStrategy should update status and expectedDelivery`() {
        val shipment = StandardShipment("s2")
        val update = ShipmentUpdateRecord("shipped", "s2", 2000L, "3000")

        ShippedStrategy().applyUpdate(shipment, update)

        assertEquals("shipped", shipment.getStatus())
        assertEquals(3000L, shipment.getExpectedDeliveryDate())
        assertEquals(1, shipment.getUpdateHistory().size)
    }

    @Test
    fun `LocationStrategy should update location`() {
        val shipment = StandardShipment("s3")
        val update = ShipmentUpdateRecord("location", "s3", 2500L, "Denver CO")

        LocationStrategy().applyUpdate(shipment, update)

        assertEquals("Denver CO", shipment.getLocation())
        assertEquals(0, shipment.getUpdateHistory().size)
    }

    @Test
    fun `DelayedStrategy should set new expected delivery date`() {
        val shipment = StandardShipment("s4")
        val update = ShipmentUpdateRecord("delayed", "s4", 3000L, "3500")

        DelayedStrategy().applyUpdate(shipment, update)

        assertEquals("delayed", shipment.getStatus())
        assertEquals(3500L, shipment.getExpectedDeliveryDate())
    }

    @Test
    fun `DeliveredStrategy should set status to delivered`() {
        val shipment = StandardShipment("s5")
        val update = ShipmentUpdateRecord("delivered", "s5", 4000L, null)

        DeliveredStrategy().applyUpdate(shipment, update)

        assertEquals("delivered", shipment.getStatus())
        assertEquals(1, shipment.getUpdateHistory().size)
    }

    @Test
    fun `CancelledStrategy should cancel shipment`() {
        val shipment = StandardShipment("s6")
        val update = ShipmentUpdateRecord("cancelled", "s6", 4500L, null)

        CancelledStrategy().applyUpdate(shipment, update)

        assertEquals("cancelled", shipment.getStatus())
    }

    @Test
    fun `LostStrategy should set status to lost`() {
        val shipment = StandardShipment("s7")
        val update = ShipmentUpdateRecord("lost", "s7", 4700L, null)

        LostStrategy().applyUpdate(shipment, update)

        assertEquals("lost", shipment.getStatus())
    }

    @Test
    fun `NoteAddedStrategy should add note`() {
        val shipment = StandardShipment("s8")
        val update = ShipmentUpdateRecord("noteadded", "s8", 5000L, "Box damaged")

        NoteAddedStrategy().applyUpdate(shipment, update)

        assertTrue(shipment.getNotes().contains("Box damaged"))
        assertEquals(1, shipment.getNotes().size)
    }

    @Test
    fun `ShippedStrategy should handle missing expectedDelivery gracefully`() {
        val shipment = StandardShipment("s9")
        val update = ShipmentUpdateRecord("shipped", "s9", 6000L, null)

        ShippedStrategy().applyUpdate(shipment, update)

        assertEquals("shipped", shipment.getStatus())
        assertEquals(0L, shipment.getExpectedDeliveryDate())
    }

    @Test
    fun `DelayedStrategy should ignore non-numeric extra`() {
        val shipment = StandardShipment("s10")
        val update = ShipmentUpdateRecord("delayed", "s10", 7000L, "not-a-number")

        DelayedStrategy().applyUpdate(shipment, update)

        assertEquals("delayed", shipment.getStatus())
        assertEquals(0L, shipment.getExpectedDeliveryDate())
    }

    @Test
    fun `Shipment should handle multiple sequential updates correctly`() {
        val shipment = StandardShipment("s11")

        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", "s11", 1000L, null))
        ShippedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("shipped", "s11", 1500L, "5000"))
        LocationStrategy().applyUpdate(shipment, ShipmentUpdateRecord("location", "s11", 1600L, "Dallas, TX"))
        NoteAddedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("noteadded", "s11", 1700L, "Left at warehouse"))
        DeliveredStrategy().applyUpdate(shipment, ShipmentUpdateRecord("delivered", "s11", 1800L, null))

        assertEquals(2, shipment.getUpdateHistory().size) // shipped, delivered
        assertEquals("shipped", shipment.getUpdateHistory()[0].newStatus)
        assertEquals("delivered", shipment.getUpdateHistory()[1].newStatus)
    }

    @Test
    fun `NoteAddedStrategy should ignore empty note`() {
        val shipment = StandardShipment("s12")
        val update = ShipmentUpdateRecord("note_added", "s12", 5100L, "")

        NoteAddedStrategy().applyUpdate(shipment, update)

        assertTrue(shipment.getNotes().isEmpty())
    }

    @Test
    fun `LocationStrategy should ignore null location`() {
        val shipment = StandardShipment("s13")
        val update = ShipmentUpdateRecord("location", "s13", 5200L, null)

        LocationStrategy().applyUpdate(shipment, update)

        assertEquals("Unknown", shipment.getLocation())
        assertEquals(0, shipment.getUpdateHistory().size)
    }

    @Test
    fun `Only status-changing strategies should appear in update history`() {
        val shipment = StandardShipment("s14")

        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", "s14", 1000L, null))
        NoteAddedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("noteadded", "s14", 1100L, "Label error"))
        LocationStrategy().applyUpdate(shipment, ShipmentUpdateRecord("location", "s14", 1200L, "Chicago"))
        DeliveredStrategy().applyUpdate(shipment, ShipmentUpdateRecord("delivered", "s14", 1300L, null))

        assertEquals(1, shipment.getUpdateHistory().size)
        assertEquals("delivered", shipment.getUpdateHistory()[0].newStatus)
    }

    @Test
    fun `CancelledStrategy should override previous status`() {
        val shipment = StandardShipment("s15")
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", "s15", 1000L, null))
        CancelledStrategy().applyUpdate(shipment, ShipmentUpdateRecord("cancelled", "s15", 1050L, null))

        assertEquals("cancelled", shipment.getStatus())
        assertEquals(1, shipment.getUpdateHistory().size)
        assertEquals("cancelled", shipment.getUpdateHistory()[0].newStatus)
    }
}
