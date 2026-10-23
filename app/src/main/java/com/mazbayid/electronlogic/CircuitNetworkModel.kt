package com.mazbayid.electronlogic

import kotlin.math.abs

data class NetworkResistor(
    val nodeA: Int,
    val nodeB: Int,
    val resistanceOhms: Float
)

data class NetworkReading(
    val nodeVoltages: List<Float>,
    val resistorCurrentsMilliAmps: List<Float>,
    val resistorPowersMilliWatts: List<Float>,
    val sourceCurrentMilliAmps: Float,
    val totalPowerMilliWatts: Float
)

/**
 * DC nodal solver for ideal voltage source V1 between node 1 and ground (node 0),
 * plus an arbitrary list of ideal resistors connected between numbered nodes.
 * A tiny ground conductance stabilizes floating nodes; this is not a SPICE solver.
 */
object CircuitNetworkModel {
    fun solve(supplyVolts: Float, highestNode: Int, resistors: List<NetworkResistor>): NetworkReading {
        val source = supplyVolts.coerceIn(0f, 30f)
        val maxNode = highestNode.coerceIn(1, 6)
        val unknownCount = (maxNode - 1).coerceAtLeast(0) // node 1 is fixed by V1; node 0 is ground
        val matrix = Array(unknownCount) { DoubleArray(unknownCount) }
        val rhs = DoubleArray(unknownCount)
        val gmin = 1e-9

        fun unknownIndex(node: Int): Int = if (node in 2..maxNode) node - 2 else -1
        fun resistance(r: NetworkResistor) = r.resistanceOhms.coerceIn(1f, 1_000_000f).toDouble()
        for (i in 0 until unknownCount) matrix[i][i] += gmin

        for (resistor in resistors) {
            val a = resistor.nodeA.coerceIn(0, maxNode)
            val b = resistor.nodeB.coerceIn(0, maxNode)
            if (a == b) continue
            val g = 1.0 / resistance(resistor)
            val ia = unknownIndex(a)
            val ib = unknownIndex(b)
            if (ia >= 0) matrix[ia][ia] += g
            if (ib >= 0) matrix[ib][ib] += g
            if (ia >= 0 && ib >= 0) {
                matrix[ia][ib] -= g
                matrix[ib][ia] -= g
            } else if (ia >= 0 && b == 1) {
                rhs[ia] += g * source
            } else if (ib >= 0 && a == 1) {
                rhs[ib] += g * source
            }
        }

        // Gaussian elimination with partial pivoting.
        for (column in 0 until unknownCount) {
            var pivot = column
            for (row in column + 1 until unknownCount) {
                if (abs(matrix[row][column]) > abs(matrix[pivot][column])) pivot = row
            }
            if (abs(matrix[pivot][column]) < 1e-15) continue
            if (pivot != column) {
                val rowTemp = matrix[column]
                matrix[column] = matrix[pivot]
                matrix[pivot] = rowTemp
                val rhsTemp = rhs[column]
                rhs[column] = rhs[pivot]
                rhs[pivot] = rhsTemp
            }
            val divisor = matrix[column][column]
            for (j in column until unknownCount) matrix[column][j] /= divisor
            rhs[column] /= divisor
            for (row in 0 until unknownCount) {
                if (row == column) continue
                val factor = matrix[row][column]
                if (factor == 0.0) continue
                for (j in column until unknownCount) matrix[row][j] -= factor * matrix[column][j]
                rhs[row] -= factor * rhs[column]
            }
        }

        val voltages = MutableList(maxNode + 1) { 0f }
        voltages[0] = 0f
        voltages[1] = source
        for (node in 2..maxNode) {
            val value = rhs[node - 2]
            voltages[node] = if (value.isFinite()) value.toFloat().coerceIn(-source, source) else 0f
        }

        val currents = resistors.map { resistor ->
            val a = resistor.nodeA.coerceIn(0, maxNode)
            val b = resistor.nodeB.coerceIn(0, maxNode)
            (voltages[a] - voltages[b]) / resistor.resistanceOhms.coerceIn(1f, 1_000_000f) * 1000f
        }
        val powers = resistors.mapIndexed { index, resistor ->
            val r = resistor.resistanceOhms.coerceIn(1f, 1_000_000f)
            currents[index] * currents[index] * r
        }
        var sourceCurrent = 0f
        resistors.forEachIndexed { index, resistor ->
            if (resistor.nodeA == 1) sourceCurrent += currents[index]
            if (resistor.nodeB == 1) sourceCurrent -= currents[index]
        }
        return NetworkReading(
            nodeVoltages = voltages,
            resistorCurrentsMilliAmps = currents,
            resistorPowersMilliWatts = powers,
            sourceCurrentMilliAmps = sourceCurrent,
            totalPowerMilliWatts = powers.sum()
        )
    }
}
