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
    fun `abnormal update is reflected in UI`() = runTest {
        val bulk = BulkShipment("bulk1")
        CreatedStrategy().applyUpdate(bulk, ShipmentUpdateRecord("created", "bulk1", 0L, null))
        TrackingServer.registerShipment(bulk.getId(), bulk)

        TrackerViewHelper.trackShipment("bulk1")

        val oneDayLater = bulk.getCreationTime() + 1 * 24 * 60 * 60 * 1000L
        bulk.delay(oneDayLater)

        bulk.notifyObservers() // <-- Fix applied

        val display = TrackerViewHelper.trackedShipments["bulk1"]
        assertNotNull(display)
        assertTrue(display.isAbnormal)
        assertTrue(display.abnormalMessage?.contains("earlier than 3-day minimum") ?: false)
    }

}
