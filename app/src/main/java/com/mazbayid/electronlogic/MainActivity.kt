package com.mazbayid.electronlogic

import android.os.Bundle
import android.graphics.Paint
import android.graphics.Typeface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF07111B)
private val Panel = Color(0xFF0D1B29)
private val Cyan = Color(0xFF4CE2EE)
private val Green = Color(0xFF53F0A2)
private val Orange = Color(0xFFFFB454)
private val Red = Color(0xFFFF596B)
private val Muted = Color(0xFF8DA7BB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7, 17, 27)
        window.navigationBarColor = android.graphics.Color.rgb(7, 17, 27)
        window.decorView.systemUiVisibility = 0
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Cyan, secondary = Green, background = Night, surface = Panel
            )) { NativeLab() }
        }
    }
}

@Composable
private fun NativeLab() {
    var tab by remember { mutableStateOf("Lab") }
    var supply by remember { mutableFloatStateOf(5f) }
    var resistance by remember { mutableFloatStateOf(330f) }
    var noise by remember { mutableStateOf(false) }
    var shortCircuit by remember { mutableStateOf(false) }
    var running by remember { mutableStateOf(true) }
    val reading = CircuitModel.calculate(supply, resistance, noise, shortCircuit)
    val current = reading.currentMilliAmps
    val output = reading.outputVolts
    val statusColor = when (reading.statusColorKey) {
        "fault" -> Red
        "noise" -> Orange
        else -> Green
    }
    val status = reading.status to statusColor
    var electronPhase by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(running, current) {
        var previousFrame = 0L
        while (true) {
            withFrameNanos { frame ->
                if (previousFrame != 0L && running) {
                    val elapsedSeconds = (frame - previousFrame) / 1_000_000_000f
                    // Illustrative drift speed follows calculated current, not a fixed loop timer.
                    val speed = (current.coerceAtLeast(0f) / 20f).coerceIn(0.025f, 1.5f)
                    electronPhase = (electronPhase + elapsedSeconds * speed) % 1f
                }
                previousFrame = frame
            }
        }
    }

    Surface(Modifier.fillMaxSize(), color = Night) {
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ϟ", color = Cyan, fontSize = 38.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("ELECTRON LOGIC", color = Color(0xFFE7F2FB), fontSize = 19.sp)
                        Text("NATIVE ANDROID · KOTLIN", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                    Text("v0.2", color = Cyan)
                }
                when (tab) {
                    "Lab" -> {
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                            Column {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("CIRCUIT OVERVIEW", color = Color(0xFFE7F2FB))
                                        Text("DC source · resistor · LED", color = Muted, fontSize = 11.sp)
                                    }
                                    Text(status.first, color = status.second, fontSize = 11.sp)
                                }
                                CircuitCanvas(Modifier.fillMaxWidth().height(240.dp), noise, shortCircuit, electronPhase, running)
                                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    ValueBlock("SOURCE", "%.1f V".format(supply), Cyan)
                                    ValueBlock("RESISTOR", resistance.toInt().toString() + " Ω", Color.White)
                                    ValueBlock("OUTPUT", "%.2f V".format(output), Cyan)
                                }
                            }
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("SIGNAL SCOPE", color = Color(0xFFE7F2FB))
                                        Text("Tegangan output · grafik ilustratif", color = Muted, fontSize = 10.sp)
                                    }
                                    Text(if (running) "RUN" else "PAUSE", color = if (running) Green else Orange, fontSize = 10.sp)
                                }
                                SignalScope(Modifier.fillMaxWidth().height(135.dp), output, supply, noise, shortCircuit, electronPhase)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Metric("ARUS", "%.2f mA".format(current), Green, Modifier.weight(1f))
                            Metric("OUTPUT", "%.2f V".format(output), Cyan, Modifier.weight(1f))
                            Metric("STATUS", status.first, status.second, Modifier.weight(1f))
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("PARAMETER LAB", color = Cyan, letterSpacing = 1.sp)
                                Row { Text("Tegangan sumber", Modifier.weight(1f), color = Color.LightGray); Text("%.1f V".format(supply), color = Cyan) }
                                Slider(supply, { supply = it }, valueRange = 1f..12f)
                                Row { Text("Resistansi", Modifier.weight(1f), color = Color.LightGray); Text(resistance.toInt().toString() + " Ω", color = Cyan) }
                                Slider(resistance, { resistance = it }, valueRange = 100f..2200f)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Animasi elektron", Modifier.weight(1f), color = Color.LightGray)
                                    Switch(checked = running, onCheckedChange = { running = it })
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { noise = !noise }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (noise) Orange.copy(alpha = .2f) else Color(0xFF13283A))) { Text(if (noise) "✓ Noise demo" else "Noise demo") }
                                    Button(onClick = { shortCircuit = !shortCircuit }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (shortCircuit) Red.copy(alpha = .2f) else Color(0xFF13283A))) { Text(if (shortCircuit) "✓ Short demo" else "Short demo") }
                                }
                                Text("Angka dan gangguan masih model demonstrasi sederhana, bukan solver fisika lengkap.", color = Muted, fontSize = 11.sp)
                            }
                        }
                    }
                    "Circuit" -> InfoCard("CIRCUIT EDITOR", "Tahap berikutnya: komponen native, pin, kabel, simpul, dan penyuntingan rangkaian bebas.")
                    else -> ScopePanel(output, supply, noise, shortCircuit, electronPhase, running)
                }
            }
            NavigationBar(containerColor = Panel) {
                listOf("Lab", "Circuit", "Measure").forEach { name ->
                    NavigationBarItem(
                        selected = tab == name,
                        onClick = { tab = name },
                        icon = { Text(when (name) { "Lab" -> "⌁"; "Circuit" -> "⌘"; else -> "∿" }) },
                        label = { Text(name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CircuitCanvas(modifier: Modifier, noise: Boolean, fault: Boolean, electronPhase: Float, running: Boolean) {
    Canvas(modifier.background(Color(0xFF091622))) {
        val l = size.width * .13f
        val r = size.width * .87f
        val t = size.height * .30f
        val b = size.height * .75f
        val m = size.width * .5f
        val midY = (t + b) / 2f
        val wire = if (fault) Red else Color(0xFF42647A)
        fun line(a: Offset, b: Offset, color: Color = wire, width: Float = 3.5f) {
            drawLine(color, a, b, strokeWidth = width, cap = StrokeCap.Round)
        }
        // Wire loop, with a clearly separated resistor on the upper branch.
        line(Offset(l, t), Offset(m - 32f, t))
        line(Offset(m + 32f, t), Offset(r, t))
        line(Offset(r, t), Offset(r, b))
        line(Offset(r, b), Offset(l, b))
        line(Offset(l, b), Offset(l, t))

        // Battery symbol: long/short plates and polarity marks.
        line(Offset(l - 12f, midY - 17f), Offset(l + 12f, midY - 17f), Cyan, 4f)
        line(Offset(l - 7f, midY + 1f), Offset(l + 7f, midY + 1f), Cyan, 4f)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(205, 224, 238)
            textSize = 11f * density
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(141, 167, 187)
            textSize = 9f * density
            textAlign = Paint.Align.CENTER
        }
        drawContext.canvas.nativeCanvas.drawText("+", l + 24f, midY - 11f, textPaint)
        drawContext.canvas.nativeCanvas.drawText("−", l + 24f, midY + 10f, textPaint)
        drawContext.canvas.nativeCanvas.drawText("BATTERY", l + 4f, b + 22f, smallPaint)

        // Resistor: standard rectangular IEC-style body, not an ambiguous zigzag.
        val resistorLeft = m - 32f
        val resistorRight = m + 32f
        drawRoundRect(
            color = Color(0xFF132C3D),
            topLeft = Offset(resistorLeft, t - 10f),
            size = androidx.compose.ui.geometry.Size(resistorRight - resistorLeft, 20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
        )
        drawRoundRect(
            color = Cyan,
            topLeft = Offset(resistorLeft, t - 10f),
            size = androidx.compose.ui.geometry.Size(resistorRight - resistorLeft, 20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
        )
        drawContext.canvas.nativeCanvas.drawText("R1 · RESISTOR", m, t - 18f, smallPaint)

        // LED represented as a diode block plus two outward light arrows, clearly labelled.
        val ledX = r
        val ledY = midY
        line(Offset(ledX, ledY - 25f), Offset(ledX, ledY - 11f), Cyan, 3f)
        line(Offset(ledX - 10f, ledY - 11f), Offset(ledX + 10f, ledY - 11f), Cyan, 3f)
        line(Offset(ledX - 10f, ledY + 11f), Offset(ledX + 10f, ledY + 11f), Cyan, 3f)
        line(Offset(ledX, ledY + 11f), Offset(ledX, ledY + 25f), Cyan, 3f)
        line(Offset(ledX - 18f, ledY - 5f), Offset(ledX - 29f, ledY - 16f), Orange, 2f)
        line(Offset(ledX - 29f, ledY - 16f), Offset(ledX - 28f, ledY - 9f), Orange, 2f)
        line(Offset(ledX - 29f, ledY - 16f), Offset(ledX - 22f, ledY - 15f), Orange, 2f)
        line(Offset(ledX - 5f, ledY - 19f), Offset(ledX - 16f, ledY - 30f), Orange, 2f)
        line(Offset(ledX - 16f, ledY - 30f), Offset(ledX - 9f, ledY - 29f), Orange, 2f)
        line(Offset(ledX - 16f, ledY - 30f), Offset(ledX - 15f, ledY - 23f), Orange, 2f)
        drawContext.canvas.nativeCanvas.drawText("LED D1", ledX - 38f, ledY + 43f, smallPaint)

        // Current-path dots are explicitly illustrative; they move continuously around the loop.
        val loopLength = 2f * (r - l) + 2f * (b - t)
        fun pointOnLoop(progress: Float): Offset {
            var distance = ((progress % 1f + 1f) % 1f) * loopLength
            val topLength = r - l
            val rightLength = b - t
            val bottomLength = r - l
            return when {
                distance < topLength -> Offset(l + distance, t)
                else -> {
                    distance -= topLength
                    if (distance < rightLength) Offset(r, t + distance)
                    else {
                        distance -= rightLength
                        if (distance < bottomLength) Offset(r - distance, b)
                        else {
                            distance -= bottomLength
                            Offset(l, b - distance)
                        }
                    }
                }
            }
        }
        for (i in 0 until 12) {
            val progress = (electronPhase + i / 12f) % 1f
            val point = pointOnLoop(progress)
            val dotColor = when {
                fault -> Red
                noise && i % 3 == 0 -> Orange
                else -> Green
            }
            if (running) drawCircle(dotColor.copy(alpha = .22f), radius = 7f, center = point)
            drawCircle(dotColor, radius = if (running) 3.5f else 2.5f, center = point)
        }
        drawContext.canvas.nativeCanvas.drawText(
            "e⁻ FLOW · ILUSTRASI",
            size.width / 2f,
            size.height - 7f,
            smallPaint
        )
        if (noise) {
            line(Offset(m + 5f, t + 24f), Offset(m + 23f, t + 12f), Orange, 2f)
            line(Offset(m + 23f, t + 12f), Offset(m + 39f, t + 25f), Orange, 2f)
        }
    }
}

@Composable
private fun ScopePanel(output: Float, supply: Float, noise: Boolean, fault: Boolean, phase: Float, running: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("SIGNAL OSCILLOSCOPE", color = Cyan, letterSpacing = 1.sp)
            Text("CH1 · tegangan output terhadap waktu", color = Muted, fontSize = 11.sp)
            SignalScope(Modifier.fillMaxWidth().height(210.dp), output, supply, noise, fault, phase)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("0 ms", color = Muted, fontSize = 10.sp)
                Text(if (running) "RUNNING · MODEL DEMO" else "PAUSED", color = if (running) Green else Orange, fontSize = 10.sp)
                Text("20 ms", color = Muted, fontSize = 10.sp)
            }
            Text(
                if (fault) "Fault demo: garis merah menandai kondisi yang disisipkan."
                else if (noise) "Noise demo: riak oranye adalah gangguan buatan, bukan noise hasil pengukuran."
                else "Sinyal DC ilustratif. Bentuk gelombang belum berasal dari solver transient.",
                color = Muted, fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun SignalScope(modifier: Modifier, output: Float, supply: Float, noise: Boolean, fault: Boolean, phase: Float) {
    Canvas(modifier.background(Color(0xFF081521))) {
        val left = 28f
        val right = size.width - 8f
        val top = 12f
        val bottom = size.height - 22f
        val plotWidth = (right - left).coerceAtLeast(1f)
        val plotHeight = (bottom - top).coerceAtLeast(1f)
        val grid = Color(0xFF1D3547)
        for (i in 0..8) {
            val x = left + plotWidth * i / 8f
            drawLine(grid, Offset(x, top), Offset(x, bottom), 1f)
        }
        for (i in 0..4) {
            val y = top + plotHeight * i / 4f
            drawLine(grid, Offset(left, y), Offset(right, y), 1f)
        }
        val maxV = supply.coerceAtLeast(1f)
        fun yFor(v: Float): Float = bottom - (v.coerceIn(0f, maxV) / maxV) * plotHeight
        val points = 180
        var previous: Offset? = null
        for (i in 0..points) {
            val xNorm = i / points.toFloat()
            val ripple = if (noise) maxV * 0.08f * kotlin.math.sin((xNorm * 16f + phase * 12f) * Math.PI.toFloat()) else 0f
            val pulse = if (fault) maxV * 0.015f else 0f
            val value = (output + ripple - pulse).coerceIn(0f, maxV)
            val point = Offset(left + plotWidth * xNorm, yFor(value))
            previous?.let { drawLine(if (fault) Red else if (noise) Orange else Cyan, it, point, 2f) }
            previous = point
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(141, 167, 187)
            textSize = 9f * density
        }
        drawContext.canvas.nativeCanvas.drawText("%.1f V".format(maxV), 3f, top + 8f, paint)
        drawContext.canvas.nativeCanvas.drawText("0 V", 3f, bottom, paint)
        drawContext.canvas.nativeCanvas.drawText("TIME →", right - 40f, size.height - 5f, paint)
    }
}

@Composable
private fun ValueBlock(title: String, value: String, color: Color) {
    Column { Text(title, color = Muted, fontSize = 9.sp); Text(value, color = color, fontSize = 12.sp) }
}

@Composable
private fun Metric(title: String, value: String, accent: Color, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(10.dp)) {
            Text(title, color = Muted, fontSize = 9.sp)
            Spacer(Modifier.height(5.dp))
            Text(value, color = accent, fontSize = 12.sp)
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Cyan, letterSpacing = 1.sp)
            Text(body, color = Color.LightGray, lineHeight = 20.sp)
            Text("DALAM PENGEMBANGAN", color = Orange, fontSize = 10.sp)
        }
    }
}
