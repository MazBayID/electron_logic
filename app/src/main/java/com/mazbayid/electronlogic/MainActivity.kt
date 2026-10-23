package com.mazbayid.electronlogic

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
    val reading = CircuitModel.calculate(supply, resistance, noise, shortCircuit)
    val current = reading.currentMilliAmps
    val output = reading.outputVolts
    val statusColor = when (reading.statusColorKey) {
        "fault" -> Red
        "noise" -> Orange
        else -> Green
    }
    val status = reading.status to statusColor

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
                                CircuitCanvas(Modifier.fillMaxWidth().height(210.dp), noise, shortCircuit)
                                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    ValueBlock("SOURCE", "%.1f V".format(supply), Cyan)
                                    ValueBlock("RESISTOR", resistance.toInt().toString() + " Ω", Color.White)
                                    ValueBlock("OUTPUT", "%.2f V".format(output), Cyan)
                                }
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
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { noise = !noise }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (noise) Orange.copy(alpha = .2f) else Color(0xFF13283A))) { Text(if (noise) "✓ Noise demo" else "Noise demo") }
                                    Button(onClick = { shortCircuit = !shortCircuit }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (shortCircuit) Red.copy(alpha = .2f) else Color(0xFF13283A))) { Text(if (shortCircuit) "✓ Short demo" else "Short demo") }
                                }
                                Text("Angka dan gangguan masih model demonstrasi sederhana, bukan solver fisika lengkap.", color = Muted, fontSize = 11.sp)
                            }
                        }
                    }
                    "Circuit" -> InfoCard("CIRCUIT EDITOR", "Tahap berikutnya: komponen native, pin, kabel, simpul, dan penyuntingan rangkaian bebas.")
                    else -> InfoCard("MEASUREMENT TOOLS", "Alat ukur dan grafik akan membaca hasil solver DC/transient setelah engine rangkaian tersedia.")
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
private fun CircuitCanvas(modifier: Modifier, noise: Boolean, fault: Boolean) {
    Canvas(modifier.background(Color(0xFF091622))) {
        val l = size.width * .14f
        val r = size.width * .86f
        val t = size.height * .27f
        val b = size.height * .73f
        val m = size.width * .5f
        val wire = if (fault) Red else Color(0xFF244257)
        fun wireLine(a: Offset, b: Offset, c: Color = wire, w: Float = 3f) =
            drawLine(c, a, b, strokeWidth = w, cap = StrokeCap.Round)
        wireLine(Offset(l,t), Offset(m-25,t))
        wireLine(Offset(m+25,t), Offset(r,t))
        wireLine(Offset(r,t), Offset(r,b))
        wireLine(Offset(r,b), Offset(l,b))
        wireLine(Offset(l,b), Offset(l,t))
        wireLine(Offset(l,(t+b)/2-15), Offset(l,(t+b)/2+15), Cyan, 3.5f)
        wireLine(Offset(l-9,(t+b)/2-5), Offset(l+9,(t+b)/2-5), Cyan, 3.5f)
        wireLine(Offset(l-5,(t+b)/2+6), Offset(l+5,(t+b)/2+6), Cyan, 3.5f)
        val zig = listOf(Offset(m-25,t),Offset(m-17,t-8),Offset(m-8,t+8),Offset(m,t-8),Offset(m+8,t+8),Offset(m+17,t-8),Offset(m+25,t))
        for (i in 0 until zig.lastIndex) wireLine(zig[i],zig[i+1],Cyan,3.5f)
        drawCircle(if (fault) Red else Green, 10f, Offset(r,t))
        drawCircle(Color.Transparent, 14f, Offset(r,t), style = androidx.compose.ui.graphics.drawscope.Stroke(2f, Cyan))
        for (i in 0 until 10) {
            val p = i / 10f
            val x = l + (r-l)*p
            drawCircle(if (fault) Red else if (noise && i % 3 == 0) Orange else Green, 3.5f, Offset(x,t))
        }
        if (noise) {
            wireLine(Offset(m+4,t+20),Offset(m+24,t+7),Orange,2f)
            wireLine(Offset(m+24,t+7),Offset(m+42,t+22),Orange,2f)
        }
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
