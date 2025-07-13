package org.example.project.model

import org.example.project.observer.ShipmentObserver
import kotlin.test.*

class ShipmentTests {

    private lateinit var shipment: Shipment

    @BeforeTest
    fun setup() {
        shipment = Shipment("test-id")
    }

    @Test
    fun `Initial shipment status should be created`() {
        assertEquals("created", shipment.getStatus())
    }

    @Test
    fun `Initial shipment location should be Unknown`() {
        assertEquals("Unknown", shipment.getLocation())
    }

    @Test
    fun `Initial expected delivery date should be 0`() {
        assertEquals(0L, shipment.getExpectedDeliveryDate())
    }

    @Test
    fun `Initial notes and update history should be empty`() {
        assertTrue(shipment.getNotes().isEmpty())
        assertTrue(shipment.getUpdateHistory().isEmpty())
    }

    @Test
    fun `updateStatus should change status and add to update history`() {
        shipment.updateStatus("shipped", 1000L)
        assertEquals("shipped", shipment.getStatus())
        assertEquals(1, shipment.getUpdateHistory().size)
        val update = shipment.getUpdateHistory()[0]
        assertEquals("created", update.previousStatus)
        assertEquals("shipped", update.newStatus)
        assertEquals(1000L, update.timestamp)
    }

    @Test
    fun `updateLocation should change location`() {
        shipment.updateLocation("New York, NY")
        assertEquals("New York, NY", shipment.getLocation())
    }

    @Test
    fun `delay should update expected delivery date`() {
        shipment.delay(9999999L)
        assertEquals(9999999L, shipment.getExpectedDeliveryDate())
    }

    @Test
    fun `addNote should add non-blank note`() {
        shipment.addNote("Package inspected")
        assertEquals(1, shipment.getNotes().size)
        assertEquals("Package inspected", shipment.getNotes()[0])
    }

    @Test
    fun `addNote should ignore blank note`() {
        shipment.addNote("")
        shipment.addNote("   ")
        assertTrue(shipment.getNotes().isEmpty())
    }

    @Test
    fun `Observers should be notified on status update`() {
        var notified = false
        val observer = object : ShipmentObserver {
            override fun onShipmentUpdated(s: Shipment) {
                notified = true
            }
        }
        shipment.addObserver(observer)
        shipment.updateStatus("shipped", 1000L)
        assertTrue(notified)
    }

    @Test
    fun `Observers should be notified on location update`() {
        var notified = false
        val observer = object : ShipmentObserver {
            override fun onShipmentUpdated(s: Shipment) {
                notified = true
            }
        }
        shipment.addObserver(observer)
        shipment.updateLocation("Denver, CO")
        assertTrue(notified)
    }

    @Test
    fun `Observers should be notified on delay`() {
        var notified = false
        val observer = object : ShipmentObserver {
            override fun onShipmentUpdated(s: Shipment) {
                notified = true
            }
        }
        shipment.addObserver(observer)
        shipment.delay(123456789L)
        assertTrue(notified)
    }

    @Test
    fun `Observers should be notified on note added`() {
        var notified = false
        val observer = object : ShipmentObserver {
            override fun onShipmentUpdated(s: Shipment) {
                notified = true
            }
        }
        shipment.addObserver(observer)
        shipment.addNote("Delayed at customs")
        assertTrue(notified)
    }

    @Test
    fun `Removed observers should not be notified`() {
        var notified = false
        val observer = object : ShipmentObserver {
            override fun onShipmentUpdated(s: Shipment) {
                notified = true
            }
        }
        shipment.addObserver(observer)
        shipment.removeObserver(observer)
        shipment.updateStatus("lost", 2000L)
        assertFalse(notified)
    }
}
