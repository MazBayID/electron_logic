package com.mazbayid.electronlogic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CircuitModelTest {
    @Test
    fun ledDemoAtFiveVoltsAnd330OhmsUsesSimpleForwardDropEstimate() {
        val reading = CircuitModel.calculate(5f, 330f, false, false)
        assertEquals(9.09f, reading.currentMilliAmps, 0.02f)
        assertEquals(2f, reading.outputVolts, 0.001f)
        assertEquals("NORMAL", reading.status)
    }

    @Test
    fun shortDemoIsClearlyMarkedAsIllustrativeFault() {
        val reading = CircuitModel.calculate(5f, 330f, false, true)
        assertEquals("FAULT DEMO", reading.status)
        assertEquals(500f, reading.currentMilliAmps, 0.001f)
        assertTrue(reading.outputVolts >= 0f)
    }

    @Test
    fun noiseDemoDoesNotClaimToBePhysicalNoiseModel() {
        val reading = CircuitModel.calculate(5f, 330f, true, false)
        assertEquals("NOISE DEMO", reading.status)
        assertEquals("noise", reading.statusColorKey)
    }
}
