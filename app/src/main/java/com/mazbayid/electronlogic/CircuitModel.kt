package com.mazbayid.electronlogic

import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

enum class CircuitScenario(val title: String) {
    LED("DC + resistor + LED"),
    RC("Pengisian kapasitor RC"),
    NE555("Timer NE555 astable")
}

data class CircuitReading(
    val currentMilliAmps: Float,
    val outputVolts: Float,
    val status: String,
    val statusColorKey: String,
    val capacitorVolts: Float = 0f,
    val frequencyHz: Float = 0f,
    val timeSeconds: Float = 0f
)

/**
 * Transparent teaching models, not a general-purpose SPICE solver.
 * The noise term is a deliberately injected signal, not physical thermal/shot noise.
 */
object CircuitModel {
    fun calculate(
        supplyVolts: Float,
        resistanceOhms: Float,
        noiseEnabled: Boolean,
        shortCircuitEnabled: Boolean,
        scenario: CircuitScenario = CircuitScenario.LED,
        timeSeconds: Float = 0f,
        noiseAmplitudeVolts: Float = 0.15f
    ): CircuitReading {
        val v = supplyVolts.coerceIn(0f, 30f)
        val r = resistanceOhms.coerceAtLeast(1f)
        val t = timeSeconds.coerceAtLeast(0f)
        val amplitude = noiseAmplitudeVolts.coerceIn(0f, 1f)
        val injectedNoise = if (noiseEnabled) {
            amplitude * (sin(t * 17f) + 0.45f * sin(t * 31f + 1.2f))
        } else 0f

        if (shortCircuitEnabled) {
            // A bounded visual fault indicator, not a prediction of real fault current.
            return CircuitReading(
                currentMilliAmps = v * 100f,
                outputVolts = 0f,
                status = "FAULT DEMO",
                statusColorKey = "fault",
                timeSeconds = t
            )
        }

        val reading = when (scenario) {
            CircuitScenario.LED -> {
                val current = ((v - 2f).coerceAtLeast(0f) / r) * 1000f
                val out = if (v >= 2f) 2f else 0f
                CircuitReading(current, (out + injectedNoise).coerceIn(0f, v), "NORMAL", "normal", timeSeconds = t)
            }
            CircuitScenario.RC -> {
                // Ideal capacitor charging from 0 V: tau = R*C, with C fixed at 1000 uF.
                val tau = (r * 0.001f).coerceAtLeast(0.001f)
                val phaseTime = t % (tau * 8f)
                val cap = (v * (1f - kotlin.math.exp(-phaseTime / tau))).coerceIn(0f, v)
                val current = ((v - cap) / r) * 1000f
                CircuitReading(
                    current, (cap + injectedNoise).coerceIn(0f, v), "NORMAL", "normal",
                    capacitorVolts = cap, timeSeconds = t
                )
            }
            CircuitScenario.NE555 -> {
                // Illustrative astable estimate: RA=RB=R and C=10 uF.
                // f ≈ 1.44 / ((RA + 2*RB) * C); thresholds nominally near 1/3 and 2/3 VCC.
                val capacitanceFarads = 10e-6f
                val frequency = 1.44f / ((3f * r) * capacitanceFarads)
                val period = 1f / frequency
                val high = (t % period) < (period * 0.693f)
                val out = if (high) v else 0f
                CircuitReading(
                    currentMilliAmps = out / r * 1000f,
                    outputVolts = (out + injectedNoise).coerceIn(0f, v),
                    status = "MODEL DEMO",
                    statusColorKey = "normal",
                    frequencyHz = frequency,
                    timeSeconds = t
                )
            }
        }
        return when {
            noiseEnabled -> reading.copy(status = "NOISE INJEKSI", statusColorKey = "noise")
            reading.currentMilliAmps > 30f -> reading.copy(status = "ARUS TINGGI", statusColorKey = "fault")
            else -> reading
        }
    }
}
