package com.mazbayid.electronlogic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CircuitModelTest {
    @Test
    fun ledDemoUsesForwardDropEstimate() {
        val reading = CircuitModel.calculate(5f, 330f, false, false)
        assertEquals(9.09f, reading.currentMilliAmps, 0.02f)
        assertEquals(2f, reading.outputVolts, 0.001f)
        assertEquals("NORMAL", reading.status)
    }

    @Test
    fun lowerSupplyDoesNotDriveTheLedModel() {
        val reading = CircuitModel.calculate(1.5f, 330f, false, false)
        assertEquals(0f, reading.currentMilliAmps, 0.001f)
        assertEquals(0f, reading.outputVolts, 0.001f)
    }

    @Test
    fun rcCapacitorStartsUnchargedAndChargesOverTime() {
        val start = CircuitModel.calculate(5f, 1000f, false, false, CircuitScenario.RC, 0f)
        val later = CircuitModel.calculate(5f, 1000f, false, false, CircuitScenario.RC, 1f)
        assertEquals(0f, start.capacitorVolts, 0.001f)
        assertTrue(later.capacitorVolts > start.capacitorVolts)
        assertTrue(later.capacitorVolts < 5f)
    }

    @Test
    fun ne555DemoExposesPositiveEstimatedFrequencyAndSwitchingOutput() {
        val first = CircuitModel.calculate(5f, 1000f, false, false, CircuitScenario.NE555, 0f)
        val later = CircuitModel.calculate(5f, 1000f, false, false, CircuitScenario.NE555, 0.02f)
        assertTrue(first.frequencyHz > 0f)
        assertTrue(first.outputVolts in 0f..5f)
        assertTrue(later.outputVolts in 0f..5f)
    }

    @Test
    fun shortDemoIsClearlyMarkedAsIllustrativeFault() {
        val reading = CircuitModel.calculate(5f, 330f, false, true)
        assertEquals("FAULT DEMO", reading.status)
        assertEquals(500f, reading.currentMilliAmps, 0.001f)
        assertEquals("fault", reading.statusColorKey)
    }

    @Test
    fun noiseIsExplicitlyLabeledAsInjectedNotPhysicalNoise() {
        val reading = CircuitModel.calculate(5f, 330f, true, false)
        assertEquals("NOISE INJEKSI", reading.status)
        assertEquals("noise", reading.statusColorKey)
    }
}
