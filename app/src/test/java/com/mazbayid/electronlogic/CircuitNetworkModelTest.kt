package com.mazbayid.electronlogic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CircuitNetworkModelTest {
    @Test
    fun oneResistorAcrossFiveVoltSourceMatchesOhmsLaw() {
        val result = CircuitNetworkModel.solve(
            5f, 1, listOf(NetworkResistor(1, 0, 1000f))
        )
        assertEquals(5f, result.nodeVoltages[1], 0.001f)
        assertEquals(5f, result.sourceCurrentMilliAmps, 0.01f)
        assertEquals(5f, result.resistorCurrentsMilliAmps[0], 0.01f)
        assertEquals(25f, result.totalPowerMilliWatts, 0.1f)
    }

    @Test
    fun twoEqualSeriesResistorsSetMidpointToHalfSupply() {
        val result = CircuitNetworkModel.solve(
            5f, 2,
            listOf(NetworkResistor(1, 2, 1000f), NetworkResistor(2, 0, 1000f))
        )
        assertEquals(2.5f, result.nodeVoltages[2], 0.01f)
        assertEquals(2.5f, result.sourceCurrentMilliAmps, 0.02f)
        assertEquals(2.5f, result.resistorCurrentsMilliAmps[0], 0.02f)
    }

    @Test
    fun parallelResistorsIncreaseTotalCurrent() {
        val result = CircuitNetworkModel.solve(
            5f, 1,
            listOf(NetworkResistor(1, 0, 1000f), NetworkResistor(1, 0, 1000f))
        )
        assertEquals(10f, result.sourceCurrentMilliAmps, 0.02f)
        assertTrue(result.totalPowerMilliWatts > 49f)
    }
}
