package org.example.project.client

import kotlinx.coroutines.test.runTest
import org.example.project.model.*
import org.example.project.server.TrackingServer
import org.example.project.strategy.*
import kotlin.test.*

class TrackerViewHelperTests {

    @BeforeTest
    fun setup() {
        TrackerViewHelper.trackedShipments.clear()
        TrackerViewHelper.trackedOrder.clear()
    }

    @Test
    fun `trackShipment returns false for unknown ID`() = runTest {
        val result = TrackerViewHelper.trackShipment("unknown123")
        assertFalse(result)
    }

    @Test
    fun `trackShipment returns true and updates reflect on tracked shipment`() = runTest {
        val id = "s1"
        val shipment = StandardShipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))
        TrackingServer.registerShipment(shipment.getId(), shipment)

        val result = TrackerViewHelper.trackShipment(id)
        assertTrue(result)

        shipment.updateStatus("shipped", 2000L)

        val display = TrackerViewHelper.trackedShipments[id]
        assertNotNull(display)
        assertEquals("shipped", display.status)
    }

    @Test
    fun `stopTracking makes shipment disappear from display`() = runTest {
        val id = "s2"
        val shipment = StandardShipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))
        TrackingServer.registerShipment(shipment.getId(), shipment)

        TrackerViewHelper.trackShipment(id)
        TrackerViewHelper.stopTracking(id)

        assertNull(TrackerViewHelper.trackedShipments[id])
        assertFalse(TrackerViewHelper.trackedOrder.contains(id))
    }

    @Test
    fun `note added to tracked shipment appears in display`() = runTest {
        val id = "s3"
        val shipment = StandardShipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))
        TrackingServer.registerShipment(shipment.getId(), shipment)

        TrackerViewHelper.trackShipment(id)

        NoteAddedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("noteadded", id, 1100L, "delayed at customs"))

        val display = TrackerViewHelper.trackedShipments[id]
        assertNotNull(display)
        assertTrue(display.notes.contains("delayed at customs"))
    }

    @Test
    fun `location change is reflected in display data`() = runTest {
        val id = "s4"
        val shipment = StandardShipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))
        TrackingServer.registerShipment(shipment.getId(), shipment)

        TrackerViewHelper.trackShipment(id)
        shipment.updateLocation("Chicago")

        val display = TrackerViewHelper.trackedShipments[id]
        assertNotNull(display)
        assertEquals("Chicago", display.location)
    }

    @Test
    fun `re-tracking shipment moves it to top`() = runTest {
        val s1 = StandardShipment("s1")
        val s2 = StandardShipment("s2")
        CreatedStrategy().applyUpdate(s1, ShipmentUpdateRecord("created", "s1", 1000L, null))
        CreatedStrategy().applyUpdate(s2, ShipmentUpdateRecord("created", "s2", 1000L, null))
        TrackingServer.registerShipment(s1.getId(), s1)
        TrackingServer.registerShipment(s2.getId(), s2)

        TrackerViewHelper.trackShipment("s1")
        TrackerViewHelper.trackShipment("s2")
        TrackerViewHelper.trackShipment("s1") // re-track s1

        assertEquals(listOf("s1", "s2"), TrackerViewHelper.trackedOrder)
    }

    @Test
    fun `tracking same shipment twice keeps it unique in order list`() = runTest {
        val id = "unique-test"
        val shipment = StandardShipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))
        TrackingServer.registerShipment(id, shipment)

        TrackerViewHelper.trackShipment(id)
        TrackerViewHelper.trackShipment(id)

        assertEquals(1, TrackerViewHelper.trackedOrder.count { it == id })
    }

    @Test
    fun `expected delivery date updates are reflected in UI`() = runTest {
        val id = "edd-test"
        val shipment = StandardShipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 0L, null))
        TrackingServer.registerShipment(id, shipment)

        TrackerViewHelper.trackShipment(id)

        val expectedMillis = System.currentTimeMillis() + 86_400_000L // +1 day
        shipment.delay(expectedMillis)

        val display = TrackerViewHelper.trackedShipments[id]
        assertNotNull(display)
        assertTrue(display.expectedDeliveryDate.contains("20")) // crude check for formatted timestamp
    }

    @Test
    fun `bulk shipment abnormal note appears in UI`() = runTest {
        val id = "bulk-abnormal"
        val shipment = BulkShipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 0L, null))
        TrackingServer.registerShipment(id, shipment)

        TrackerViewHelper.trackShipment(id)
        shipment.delay(shipment.getCreationTime() + 1 * 24 * 60 * 60 * 1000L) // 1 day

        val display = TrackerViewHelper.trackedShipments[id]
        assertNotNull(display)
        assertTrue(display.notes.any { it.contains("earlier than 3-day minimum", ignoreCase = true) })
    }

    @Test
    fun `stopTracking silently ignores unknown IDs`() = runTest {
        // Should not throw
        TrackerViewHelper.stopTracking("nonexistent-id")
    }

    @Test
    fun `multiple updates and notes accumulate correctly`() = runTest {
        val id = "multi-update"
        val shipment = StandardShipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 0L, null))
        TrackingServer.registerShipment(id, shipment)

        TrackerViewHelper.trackShipment(id)

        shipment.updateStatus("shipped", 1000L)
        shipment.addNote("first note")
        shipment.updateStatus("delivered", 2000L)
        shipment.addNote("final note")

        val display = TrackerViewHelper.trackedShipments[id]
        assertNotNull(display)
        assertEquals(2, display.notes.size)
        assertEquals(2, display.updates.size) // created, shipped, delivered
    }

    @Test
    fun `most recent tracked shipment is first in order list`() = runTest {
        val id1 = "first"
        val id2 = "second"
        val s1 = StandardShipment(id1)
        val s2 = StandardShipment(id2)
        CreatedStrategy().applyUpdate(s1, ShipmentUpdateRecord("created", id1, 0L, null))
        CreatedStrategy().applyUpdate(s2, ShipmentUpdateRecord("created", id2, 0L, null))
        TrackingServer.registerShipment(id1, s1)
        TrackingServer.registerShipment(id2, s2)

        TrackerViewHelper.trackShipment(id1)
        TrackerViewHelper.trackShipment(id2)

        assertEquals(listOf(id2, id1), TrackerViewHelper.trackedOrder)
    }

}
