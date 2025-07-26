package org.example.project.model

import org.example.project.factory.BulkShipment
import org.example.project.factory.ExpressShipment
import org.example.project.factory.OvernightShipment
import org.example.project.factory.StandardShipment
import org.example.project.observer.ShipmentObserver
import kotlin.test.*

class ShipmentTests {

    private lateinit var shipment: StandardShipment

    @BeforeTest
    fun setup() {
        shipment = StandardShipment("test-id")
    }

    @Test
    fun `Initial shipment status should be empty`() {
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

    @Test
    fun `Overnight shipment warns on shipped date not 1 day after creation`() {
        val overnight = OvernightShipment("o1")
        val createdTime = overnight.getCreationTime()
        val twoDaysLater = createdTime + 2 * 24 * 60 * 60 * 1000L

        overnight.shipped(expected = twoDaysLater, timestamp = createdTime + 1000L)

        assertTrue(overnight.getNotes().any { it.contains("not exactly 24 hours") })
    }

    @Test
    fun `Overnight shipment delay beyond 1 day is allowed`() {
        val overnight = OvernightShipment("o2")
        val createdTime = overnight.getCreationTime()
        val delayedTime = createdTime + 3 * 24 * 60 * 60 * 1000L

        overnight.delay(delayedTime)

        assertTrue(overnight.getNotes().isEmpty())
    }

    @Test
    fun `Express shipment warns if delivery exceeds 3 days`() {
        val express = ExpressShipment("e1")
        val created = express.getCreationTime()
        val tooLate = created + 4 * 24 * 60 * 60 * 1000L

        express.delay(tooLate)

        assertTrue(express.getNotes().any { it.contains("exceeds 3-day limit") })
    }

    @Test
    fun `Express shipment delay within 3 days is allowed`() {
        val express = ExpressShipment("e2")
        val created = express.getCreationTime()
        val inTime = created + 2 * 24 * 60 * 60 * 1000L

        express.delay(inTime)

        assertTrue(express.getNotes().isEmpty())
    }

    @Test
    fun `Bulk shipment warns if delivery is sooner than 3 days`() {
        val bulk = BulkShipment("b1")
        val created = bulk.getCreationTime()
        val tooSoon = created + 1 * 24 * 60 * 60 * 1000L

        bulk.delay(tooSoon)

        assertTrue(bulk.getNotes().any { it.contains("earlier than 3-day minimum") })
    }

    @Test
    fun `Bulk shipment with valid delivery date is allowed`() {
        val bulk = BulkShipment("b2")
        val created = bulk.getCreationTime()
        val valid = created + 3 * 24 * 60 * 60 * 1000L

        bulk.delay(valid)

        assertTrue(bulk.getNotes().isEmpty())
    }

    @Test
    fun `Observer is notified for express shipment`() {
        val express = ExpressShipment("obs-express")
        var called = false
        express.addObserver(object : ShipmentObserver {
            override fun onShipmentUpdated(s: Shipment) {
                called = true
            }
        })
        express.delay(express.getCreationTime() + 1000L)
        assertTrue(called)
    }
}
