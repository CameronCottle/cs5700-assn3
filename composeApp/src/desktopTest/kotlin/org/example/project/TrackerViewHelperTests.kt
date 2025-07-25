package org.example.project.client

import kotlinx.coroutines.test.runTest
import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdateRecord
import org.example.project.server.TrackingServer
import org.example.project.strategy.*
import org.example.project.client.TrackerViewHelper
import kotlin.test.*

class TrackerViewHelperTests {

    private val helper = TrackerViewHelper

    @Test
    fun `trackShipment returns false for unknown ID`() = runTest {
        val result = helper.trackShipment("unknown123")
        assertFalse(result)
    }

    @Test
    fun `trackShipment returns true and updates reflect on tracked shipment`() = runTest {
        val id = "s1"
        val shipment = Shipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))

        TrackingServer.registerShipment(id, shipment)

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
        val shipment = Shipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))
        TrackingServer.registerShipment(id, shipment)

        TrackerViewHelper.trackShipment(id)
        TrackerViewHelper.stopTracking(id)

        val display = TrackerViewHelper.trackedShipments[id]
        assertNull(display)
    }

    @Test
    fun `note added to tracked shipment appears in display`() = runTest {
        val id = "s3"
        val shipment = Shipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))
        TrackingServer.registerShipment(id, shipment)

        TrackerViewHelper.trackShipment(id)

        NoteAddedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("noteadded", id, 1100L, "delayed at customs"))

        val display = TrackerViewHelper.trackedShipments[id]
        assertNotNull(display)
        assertTrue(display.notes.contains("delayed at customs"))
    }

    @Test
    fun `location change is reflected in display data`() = runTest {
        val id = "s4"
        val shipment = Shipment(id)
        CreatedStrategy().applyUpdate(shipment, ShipmentUpdateRecord("created", id, 1000L, null))
        TrackingServer.registerShipment(id, shipment)

        TrackerViewHelper.trackShipment(id)
        shipment.updateLocation("Chicago")

        val display = TrackerViewHelper.trackedShipments[id]
        assertNotNull(display)
        assertEquals("Chicago", display.location)
    }

}
