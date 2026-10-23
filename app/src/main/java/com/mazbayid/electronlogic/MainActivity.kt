package com.mazbayid.electronlogic

import android.graphics.Paint
import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.max

private val Night = Color(0xFF07111B)
private val Panel = Color(0xFF0D1B29)
private val Cyan = Color(0xFF4CE2EE)
private val Green = Color(0xFF53F0A2)
private val Orange = Color(0xFFFFB454)
private val Red = Color(0xFFFF596B)
private val Blue = Color(0xFF70A9FF)
private val Muted = Color(0xFF8DA7BB)
private val Ink = Color(0xFFE7F2FB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7, 17, 27)
        window.navigationBarColor = android.graphics.Color.rgb(7, 17, 27)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Cyan, secondary = Green, background = Night, surface = Panel)) {
                ElectronLogicApp()
            }
        }
    }
}

@Composable
private fun ElectronLogicApp() {
    var tab by remember { mutableStateOf("Lab") }
    var scenario by remember { mutableStateOf(CircuitScenario.LED) }
    var supply by remember { mutableFloatStateOf(5f) }
    var resistance by remember { mutableFloatStateOf(330f) }
    var noiseAmplitude by remember { mutableFloatStateOf(0.15f) }
    var noise by remember { mutableStateOf(false) }
    var shortCircuit by remember { mutableStateOf(false) }
    var running by remember { mutableStateOf(true) }
    var elapsed by remember { mutableFloatStateOf(0f) }
    val history = remember { mutableStateListOf<Float>() }
    val reading = CircuitModel.calculate(supply, resistance, noise, shortCircuit, scenario, elapsed, noiseAmplitude)
    val statusColor = when (reading.statusColorKey) { "fault" -> Red; "noise" -> Orange; else -> Green }

    LaunchedEffect(running) {
        while (running) {
            delay(40)
            elapsed += 0.04f
            history.add(CircuitModel.calculate(supply, resistance, noise, shortCircuit, scenario, elapsed, noiseAmplitude).outputVolts)
            if (history.size > 180) history.removeAt(0)
        }
    }

    Surface(Modifier.fillMaxSize(), color = Night) {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ϟ", color = Cyan, fontSize = 38.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("ELECTRON LOGIC", Modifier.testTag("app-title"), color = Ink, fontSize = 19.sp)
                        Text("CIRCUIT LAB · MODEL EDUKASI", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                    Text("v0.3", color = Cyan, fontSize = 12.sp)
                }
                when (tab) {
                    "Lab" -> {
                        SectionCard("CIRCUIT OVERVIEW", scenario.title) {
                            CircuitDiagram(scenario, supply, resistance, reading.outputVolts, reading.currentMilliAmps, noise, shortCircuit, running, elapsed)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ValueBlock("SUMBER", "%.1f V".format(supply), Cyan)
                                ValueBlock("ARUS MODEL", "%.2f mA".format(reading.currentMilliAmps), Green)
                                ValueBlock("OUTPUT", "%.2f V".format(reading.outputVolts), Blue)
                            }
                            HorizontalDivider(color = Color(0xFF1D3547))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("STATUS", color = Muted, fontSize = 10.sp)
                                Spacer(Modifier.width(10.dp))
                                Text(reading.status, color = statusColor, fontSize = 12.sp)
                                Spacer(Modifier.weight(1f))
                                Text(if (running) "RUN" else "PAUSE", color = if (running) Green else Orange, fontSize = 10.sp)
                            }
                        }
                        SectionCard("SIGNAL SCOPE", "Output terhadap waktu · data model yang sama") {
                            SignalScope(Modifier.fillMaxWidth().height(155.dp), history.toList(), supply, reading.outputVolts, statusColor)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0 V", color = Muted, fontSize = 10.sp)
                                Text("t = %.2f s".format(elapsed), color = Cyan, fontSize = 10.sp)
                                Text("%.1f V max".format(supply), color = Muted, fontSize = 10.sp)
                            }
                        }
                        SectionCard("PARAMETER LAB", "Ubah nilai dan amati respons model") {
                            ScenarioPicker(scenario) { scenario = it; elapsed = 0f; history.clear() }
                            ParameterSlider("Tegangan sumber", "%.1f V".format(supply), supply, 1f, 12f, { supply = it })
                            ParameterSlider("Resistansi / R", resistance.toInt().toString() + " Ω", resistance, 100f, 2200f, { resistance = it }, "resistance-slider")
                            ParameterSlider("Amplitudo noise injeksi", "%.2f V".format(noiseAmplitude), noiseAmplitude, 0f, 1f, { noiseAmplitude = it })
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Jalankan simulasi", Modifier.weight(1f), color = Ink)
                                Switch(checked = running, onCheckedChange = { running = it })
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { noise = !noise }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (noise) Orange.copy(alpha = .22f) else Color(0xFF13283A))) {
                                    Text(if (noise) "Noise: ON" else "Noise: OFF")
                                }
                                Button(onClick = { shortCircuit = !shortCircuit }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (shortCircuit) Red.copy(alpha = .22f) else Color(0xFF13283A))) {
                                    Text(if (shortCircuit) "Fault: ON" else "Fault demo")
                                }
                            }
                            OutlinedButton(onClick = {
                                elapsed = 0f; history.clear(); supply = 5f; resistance = 330f
                                noiseAmplitude = 0.15f; noise = false; shortCircuit = false; running = true; scenario = CircuitScenario.LED
                            }, modifier = Modifier.fillMaxWidth()) { Text("↺ Reset simulasi") }
                            Text("Noise adalah sinyal gangguan yang sengaja diinjeksi. Fault demo bukan perhitungan arus hubung-singkat nyata.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                        }
                    }
                    "Circuit" -> {
                        SectionCard("CIRCUIT VIEW", "Simbol komponen standar dan model aktif") {
                            ScenarioPicker(scenario) { scenario = it; elapsed = 0f; history.clear() }
                            CircuitDiagram(scenario, supply, resistance, reading.outputVolts, reading.currentMilliAmps, noise, shortCircuit, running, elapsed)
                            Text(scenarioExplanation(scenario), color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
                            Text("Editor bebas untuk menambah, memindah, dan menyambung pin belum tersedia pada versi ini.", color = Muted, fontSize = 11.sp)
                        }
                        SectionCard("MODEL & BATASAN", "Gunakan sebagai alat belajar konsep") {
                            Text(modelNotes(scenario), color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                    else -> {
                        SectionCard("SIGNAL OSCILLOSCOPE", "CH1 · tegangan output terhadap waktu") {
                            SignalScope(Modifier.fillMaxWidth().height(250.dp), history.toList(), supply, reading.outputVolts, statusColor)
                            MetricRow("CH1", "%.2f V".format(reading.outputVolts), Cyan, "ARUS", "%.2f mA".format(reading.currentMilliAmps), Green)
                            MetricRow("TIME", "%.2f s".format(elapsed), Blue, "STATUS", reading.status, statusColor)
                            if (scenario == CircuitScenario.NE555) {
                                Text("Frekuensi astable perkiraan: %.2f Hz".format(reading.frequencyHz), color = Cyan)
                                Text("Pin 5 adalah CONTROL VOLTAGE: tegangan ini dapat mengubah ambang internal. Noise pada pin 5 bisa memodulasi timing; pin 5 bukan sumber noise.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Akuisisi berjalan", Modifier.weight(1f), color = Ink)
                                Switch(checked = running, onCheckedChange = { running = it })
                            }
                            OutlinedButton(onClick = { elapsed = 0f; history.clear() }, modifier = Modifier.fillMaxWidth()) { Text("Clear waveform") }
                        }
                        SectionCard("PENGUKURAN", "Nilai dihitung oleh model demonstrasi") {
                            Text("Sumber: %.2f V".format(supply), color = Ink)
                            Text("Resistansi: " + resistance.toInt() + " Ω", color = Ink)
                            Text("Arus model: %.3f mA".format(reading.currentMilliAmps), color = Green)
                            Text("Tegangan output: %.3f V".format(reading.outputVolts), color = Cyan)
                            Text(modelNotes(scenario), color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }
            NavigationBar(containerColor = Panel) {
                listOf("Lab", "Circuit", "Measure").forEach { name ->
                    NavigationBarItem(selected = tab == name, onClick = { tab = name }, icon = {
                        Text(when (name) { "Lab" -> "⌁"; "Circuit" -> "⌘"; else -> "∿" })
                    }, label = { Text(name) })
                }
            }
        }
    }
}

@Composable
private fun ScenarioPicker(selected: CircuitScenario, onSelected: (CircuitScenario) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text("CONTOH RANGKAIAN", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected.title, Modifier.weight(1f))
                Text("▾")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CircuitScenario.values().forEach { option ->
                    DropdownMenuItem(text = { Text(option.title) }, onClick = { onSelected(option); expanded = false })
                }
            }
        }
    }
}

@Composable
private fun ParameterSlider(title: String, valueLabel: String, value: Float, min: Float, max: Float, onChange: (Float) -> Unit, tag: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, Modifier.weight(1f), color = Ink, fontSize = 12.sp)
            Text(valueLabel, color = Cyan, fontSize = 12.sp)
        }
        val modifier = if (tag == null) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().testTag(tag)
        Slider(value = value, onValueChange = onChange, valueRange = min..max, modifier = modifier)
    }
}

@Composable
private fun SectionCard(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = Cyan, letterSpacing = 1.sp, fontSize = 12.sp)
                Text(subtitle, color = Muted, fontSize = 11.sp)
            }
            content()
        }
    }
}

@Composable
private fun CircuitDiagram(scenario: CircuitScenario, supply: Float, resistance: Float, output: Float, current: Float, noise: Boolean, fault: Boolean, running: Boolean, time: Float) {
    Canvas(Modifier.fillMaxWidth().height(235.dp).background(Color(0xFF081521))) {
        val left = size.width * .13f
        val right = size.width * .87f
        val top = size.height * .27f
        val bottom = size.height * .72f
        val mid = size.width * .5f
        val centerY = (top + bottom) / 2f
        val wire = if (fault) Red else Color(0xFF42647A)
        fun line(a: Offset, b: Offset, color: Color = wire, width: Float = 3f) {
            drawLine(color, a, b, strokeWidth = width, cap = StrokeCap.Round)
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(205, 224, 238); textSize = 10f * density; textAlign = Paint.Align.CENTER
        }
        val mutedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(141, 167, 187); textSize = 9f * density; textAlign = Paint.Align.CENTER
        }
        fun label(text: String, x: Float, y: Float, color: Color = Muted) {
            paint.color = color.toArgbAndroid()
            drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
        }
        line(Offset(left, top), Offset(mid - 34f, top))
        line(Offset(mid + 34f, top), Offset(right, top))
        line(Offset(right, top), Offset(right, bottom))
        line(Offset(right, bottom), Offset(left, bottom))
        line(Offset(left, bottom), Offset(left, top))
        // IEC battery: unequal parallel plates and polarity marks.
        line(Offset(left - 12f, centerY - 15f), Offset(left + 12f, centerY - 15f), Cyan, 4f)
        line(Offset(left - 7f, centerY + 2f), Offset(left + 7f, centerY + 2f), Cyan, 4f)
        label("+", left + 23f, centerY - 10f, Ink)
        label("−", left + 23f, centerY + 11f, Ink)
        label("V1 · DC SOURCE", left + 5f, bottom + 19f, Cyan)

        when (scenario) {
            CircuitScenario.LED -> {
                drawRect(Color(0xFF132C3D), Offset(mid - 34f, top - 10f), androidx.compose.ui.geometry.Size(68f, 20f))
                drawRect(Cyan, Offset(mid - 34f, top - 10f), androidx.compose.ui.geometry.Size(68f, 20f), style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f))
                label("R1 · RESISTOR", mid, top - 17f, Ink)
                // Standard diode bar/triangle-like wedge plus outward light arrows.
                line(Offset(right, centerY - 25f), Offset(right, centerY + 25f), Cyan, 3f)
                line(Offset(right - 15f, centerY - 14f), Offset(right, centerY), Cyan, 3f)
                line(Offset(right - 15f, centerY + 14f), Offset(right, centerY), Cyan, 3f)
                line(Offset(right - 15f, centerY - 14f), Offset(right - 15f, centerY + 14f), Cyan, 3f)
                line(Offset(right - 25f, centerY - 21f), Offset(right - 34f, centerY - 30f), Orange, 2f)
                line(Offset(right - 34f, centerY - 30f), Offset(right - 26f, centerY - 29f), Orange, 2f)
                line(Offset(right - 25f, centerY - 4f), Offset(right - 34f, centerY - 13f), Orange, 2f)
                label("D1 · LED", right - 10f, centerY + 43f, Ink)
            }
            CircuitScenario.RC -> {
                drawRect(Color(0xFF132C3D), Offset(mid - 34f, top - 10f), androidx.compose.ui.geometry.Size(68f, 20f))
                drawRect(Cyan, Offset(mid - 34f, top - 10f), androidx.compose.ui.geometry.Size(68f, 20f), style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f))
                label("R1", mid, top - 17f, Ink)
                line(Offset(right - 13f, centerY - 14f), Offset(right + 13f, centerY - 14f), Blue, 4f)
                line(Offset(right - 13f, centerY + 14f), Offset(right + 13f, centerY + 14f), Blue, 4f)
                label("C1 · 1000 µF", right - 15f, centerY + 36f, Blue)
            }
            CircuitScenario.NE555 -> {
                drawRect(Color(0xFF132C3D), Offset(right - 34f, centerY - 38f), androidx.compose.ui.geometry.Size(68f, 76f))
                drawRect(Cyan, Offset(right - 34f, centerY - 38f), androidx.compose.ui.geometry.Size(68f, 76f), style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f))
                label("NE555", right, centerY + 4f, Ink)
                label("2/3 VCC", right - 5f, centerY - 46f, Blue)
                label("1/3 VCC", right - 5f, centerY + 53f, Blue)
                label("RA = RB = R", mid, top - 17f, Ink)
                label("OUT", mid, bottom + 18f, Cyan)
            }
        }
        // Schematic markers only; they follow calculated current and are not individual electrons.
        if (running && current > 0f) {
            val speed = (current / 20f).coerceIn(0f, 1.5f)
            for (i in 0 until 10) {
                val p = (time * speed * 0.12f + i / 10f) % 1f
                val point = when {
                    p < .25f -> Offset(left + (right - left) * (p * 4f), top)
                    p < .5f -> Offset(right, top + (bottom - top) * ((p - .25f) * 4f))
                    p < .75f -> Offset(right - (right - left) * ((p - .5f) * 4f), bottom)
                    else -> Offset(left, bottom - (bottom - top) * ((p - .75f) * 4f))
                }
                drawCircle(if (fault) Red else if (noise && i % 3 == 0) Orange else Green, 3f, point)
            }
        }
        if (noise) {
            line(Offset(mid + 6f, top + 24f), Offset(mid + 20f, top + 13f), Orange, 2f)
            line(Offset(mid + 20f, top + 13f), Offset(mid + 34f, top + 25f), Orange, 2f)
            mutedPaint.color = android.graphics.Color.rgb(255, 180, 84)
            drawContext.canvas.nativeCanvas.drawText("NOISE INJEKSI", mid + 18f, top + 43f, mutedPaint)
        }
        drawContext.canvas.nativeCanvas.drawText(
            if (scenario == CircuitScenario.NE555) "f ≈ %.1f Hz · MODEL".format(CircuitModel.calculate(supply, resistance, noise, fault, scenario, time).frequencyHz)
            else "R1 = %.0f Ω · I = %.2f mA".format(resistance, current),
            size.width / 2f, size.height - 5f, mutedPaint
        )
    }
}

@Composable
private fun SignalScope(modifier: Modifier, history: List<Float>, supply: Float, currentOutput: Float, traceColor: Color) {
    Canvas(modifier.background(Color(0xFF081521))) {
        val left = 30f
        val right = size.width - 8f
        val top = 12f
        val bottom = size.height - 22f
        val plotWidth = (right - left).coerceAtLeast(1f)
        val plotHeight = (bottom - top).coerceAtLeast(1f)
        val maxV = max(supply, 1f)
        val grid = Color(0xFF1D3547)
        for (i in 0..8) drawLine(grid, Offset(left + plotWidth * i / 8f, top), Offset(left + plotWidth * i / 8f, bottom), 1f)
        for (i in 0..4) drawLine(grid, Offset(left, top + plotHeight * i / 4f), Offset(right, top + plotHeight * i / 4f), 1f)
        fun yFor(v: Float) = bottom - (v.coerceIn(0f, maxV) / maxV) * plotHeight
        val samples = if (history.isEmpty()) listOf(currentOutput) else history
        var previous: Offset? = null
        samples.forEachIndexed { index, value ->
            val x = left + plotWidth * index / max(1, samples.lastIndex).toFloat()
            val point = Offset(x, yFor(value))
            previous?.let { drawLine(traceColor, it, point, 2.2f, cap = StrokeCap.Round) }
            previous = point
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.rgb(141, 167, 187); textSize = 9f * density }
        drawContext.canvas.nativeCanvas.drawText("%.1f V".format(maxV), 2f, top + 8f, paint)
        drawContext.canvas.nativeCanvas.drawText("0 V", 9f, bottom, paint)
        drawContext.canvas.nativeCanvas.drawText("TIME →", right - 42f, size.height - 5f, paint)
    }
}

@Composable
private fun ValueBlock(title: String, value: String, color: Color) {
    Column { Text(title, color = Muted, fontSize = 9.sp); Text(value, color = color, fontSize = 12.sp) }
}

@Composable
private fun MetricRow(labelA: String, valueA: String, colorA: Color, labelB: String, valueB: String, colorB: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF091622))) {
            Column(Modifier.padding(10.dp)) { Text(labelA, color = Muted, fontSize = 10.sp); Text(valueA, color = colorA, fontSize = 14.sp) }
        }
        Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF091622))) {
            Column(Modifier.padding(10.dp)) { Text(labelB, color = Muted, fontSize = 10.sp); Text(valueB, color = colorB, fontSize = 14.sp) }
        }
    }
}

private fun scenarioExplanation(scenario: CircuitScenario): String = when (scenario) {
    CircuitScenario.LED -> "R1 membatasi arus LED. Hasil menggunakan pendekatan tegangan maju tetap 2 V, bukan model LED berdasarkan datasheet."
    CircuitScenario.RC -> "Kapasitor ideal diisi melalui resistor. Tegangan kapasitor meningkat secara eksponensial; muatan terkumpul pada dua pelat dan elektron tidak menyeberangi dielektrik."
    CircuitScenario.NE555 -> "Astable NE555 menghasilkan keluaran berulang. Ambang internal nominal sekitar 1/3 dan 2/3 VCC; pin 5 adalah CONTROL VOLTAGE dan dapat memodulasi ambang."
}

private fun modelNotes(scenario: CircuitScenario): String = when (scenario) {
    CircuitScenario.LED -> "Model DC: Vf LED diasumsikan 2 V. Tidak menghitung suhu, toleransi, resistansi dinamis, atau karakteristik datasheet. Arus tinggi hanya penanda edukasi."
    CircuitScenario.RC -> "Model RC: C tetap 1000 µF, kapasitor ideal, pengisian berulang sederhana dengan tau = R×C. ESR, leakage, jaringan bebas, dan kondisi awal yang dapat diedit belum dimodelkan."
    CircuitScenario.NE555 -> "Model NE555: perkiraan astable memakai RA=RB=R dan C=10 µF. Belum mensimulasikan transistor internal, toleransi, beban output, jitter, atau respons pin 5 secara listrik."
}

private fun Color.toArgbAndroid(): Int = android.graphics.Color.argb((alpha * 255).toInt(), (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
