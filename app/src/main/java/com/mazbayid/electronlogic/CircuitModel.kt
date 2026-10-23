package com.mazbayid.electronlogic

/**
 * Small, explicitly illustrative DC model used by the first native UI.
 * This is not a general circuit solver and does not model real noise physics.
 */
data class CircuitReading(
    val currentMilliAmps: Float,
    val outputVolts: Float,
    val status: String,
    val statusColorKey: String
)

object CircuitModel {
    fun calculate(
        supplyVolts: Float,
        resistanceOhms: Float,
        noiseEnabled: Boolean,
        shortCircuitEnabled: Boolean
    ): CircuitReading {
        val safeSupply = supplyVolts.coerceAtLeast(0f)
        val safeResistance = resistanceOhms.coerceAtLeast(1f)

        if (shortCircuitEnabled) {
            // Deliberately bounded display value for a fault demo, not a real short model.
            return CircuitReading(
                currentMilliAmps = safeSupply * 100f,
                outputVolts = 0.1f,
                status = "FAULT DEMO",
                statusColorKey = "fault"
            )
        }

        val current = ((safeSupply - 2f).coerceAtLeast(0f) / safeResistance) * 1000f
        val output = (safeSupply - current * safeResistance / 1000f).coerceAtLeast(0f)

        return when {
            noiseEnabled -> CircuitReading(current, output, "NOISE DEMO", "noise")
            current > 30f -> CircuitReading(current, output, "ARUS TINGGI", "fault")
            else -> CircuitReading(current, output, "NORMAL", "normal")
        }
    }
}
