package org.example.project.factory

import org.example.project.model.*
import kotlin.test.*

class ShipmentFactoryTests {

    @Test
    fun `factory creates StandardShipment`() {
        val shipment = ShipmentFactory.createShipment("s1", "standard")
        assertTrue(shipment is StandardShipment)
        assertEquals("s1", shipment.getId())
    }

    @Test
    fun `factory creates BulkShipment`() {
        val shipment = ShipmentFactory.createShipment("s2", "bulk")
        assertTrue(shipment is BulkShipment)
    }

    @Test
    fun `factory creates ExpressShipment`() {
        val shipment = ShipmentFactory.createShipment("s3", "express")
        assertTrue(shipment is ExpressShipment)
    }

    @Test
    fun `factory creates OvernightShipment`() {
        val shipment = ShipmentFactory.createShipment("s4", "overnight")
        assertTrue(shipment is OvernightShipment)
    }

    @Test
    fun `factory handles case-insensitive shipment type`() {
        val shipment = ShipmentFactory.createShipment("s5", "ExPreSS")
        assertTrue(shipment is ExpressShipment)
    }

    @Test
    fun `factory throws on unknown shipment type`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            ShipmentFactory.createShipment("s6", "unknownType")
        }
        assertTrue(exception.message!!.contains("Unknown shipment type"))
    }
}
