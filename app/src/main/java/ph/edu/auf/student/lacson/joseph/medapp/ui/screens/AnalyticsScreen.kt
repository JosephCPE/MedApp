package ph.edu.auf.student.lacson.joseph.medapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthLog
import ph.edu.auf.student.lacson.joseph.medapp.ui.viewmodels.HealthLogViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: HealthLogViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedPeriod by remember { mutableIntStateOf(30) }

    val healthLogs by viewModel.healthLogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHealthLogs()
    }

    val filteredLogs = remember(healthLogs, selectedPeriod) {
        val cutoffDate = System.currentTimeMillis() - (selectedPeriod * 24 * 60 * 60 * 1000L)
        healthLogs.filter { it.date >= cutoffDate }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Blood Pressure") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Heart Rate") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Weight") }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedPeriod == 7,
                    onClick = { selectedPeriod = 7 },
                    label = { Text("7d") }
                )
                FilterChip(
                    selected = selectedPeriod == 30,
                    onClick = { selectedPeriod = 30 },
                    label = { Text("30d") }
                )
                FilterChip(
                    selected = selectedPeriod == 90,
                    onClick = { selectedPeriod = 90 },
                    label = { Text("90d") }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available for the selected period")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                when (selectedTab) {
                                    0 -> BloodPressureChart(filteredLogs)
                                    1 -> HeartRateChart(filteredLogs)
                                    2 -> WeightChart(filteredLogs)
                                }
                            }
                        }
                    }
                    item {
                        Text(
                            text = "Recent Entries",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(filteredLogs.take(10)) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(log.date)),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                when (selectedTab) {
                                    0 -> Text("BP: ${log.systolic}/${log.diastolic} mmHg")
                                    1 -> Text("Heart Rate: ${log.heartRate} BPM")
                                    2 -> Text("Weight: ${log.weight} kg")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BloodPressureChart(logs: List<HealthLog>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (logs.isEmpty()) return@Canvas

        val sortedLogs = logs.sortedBy { it.date }
        val systolicData = sortedLogs.map { it.systolic.toFloat() }
        val diastolicData = sortedLogs.map { it.diastolic.toFloat() }

        val maxValue = max(systolicData.maxOrNull() ?: 0f, diastolicData.maxOrNull() ?: 0f)
        val minValue = 0f

        val width = size.width
        val height = size.height
        val spacing = width / (systolicData.size - 1).coerceAtLeast(1)

        val systolicPath = Path()
        val diastolicPath = Path()

        systolicData.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minValue) / (maxValue - minValue) * height)

            if (index == 0) {
                systolicPath.moveTo(x, y)
            } else {
                systolicPath.lineTo(x, y)
            }
        }

        diastolicData.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minValue) / (maxValue - minValue) * height)

            if (index == 0) {
                diastolicPath.moveTo(x, y)
            } else {
                diastolicPath.lineTo(x, y)
            }
        }

        drawPath(
            path = systolicPath,
            color = primaryColor,
            style = Stroke(width = 4f)
        )

        drawPath(
            path = diastolicPath,
            color = secondaryColor,
            style = Stroke(width = 4f)
        )

        systolicData.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minValue) / (maxValue - minValue) * height)
            drawCircle(
                color = primaryColor,
                radius = 6f,
                center = Offset(x, y)
            )
        }

        diastolicData.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minValue) / (maxValue - minValue) * height)
            drawCircle(
                color = secondaryColor,
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun HeartRateChart(logs: List<HealthLog>) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (logs.isEmpty()) return@Canvas

        val sortedLogs = logs.sortedBy { it.date }
        val heartRateData = sortedLogs.map { it.heartRate.toFloat() }

        val maxValue = heartRateData.maxOrNull() ?: 100f
        val minValue = heartRateData.minOrNull()?.minus(10f) ?: 0f

        val width = size.width
        val height = size.height
        val spacing = width / (heartRateData.size - 1).coerceAtLeast(1)

        val path = Path()

        heartRateData.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minValue) / (maxValue - minValue) * height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 4f)
        )

        heartRateData.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minValue) / (maxValue - minValue) * height)
            drawCircle(
                color = primaryColor,
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun WeightChart(logs: List<HealthLog>) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (logs.isEmpty()) return@Canvas

        val sortedLogs = logs.sortedBy { it.date }
        val weightData = sortedLogs.map { it.weight.toFloat() }

        val maxValue = weightData.maxOrNull()?.plus(5f) ?: 100f
        val minValue = (weightData.minOrNull()?.minus(5f) ?: 0f).coerceAtLeast(0f)

        val width = size.width
        val height = size.height
        val spacing = width / (weightData.size - 1).coerceAtLeast(1)

        val path = Path()

        weightData.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minValue) / (maxValue - minValue) * height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 4f)
        )

        weightData.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value - minValue) / (maxValue - minValue) * height)
            drawCircle(
                color = primaryColor,
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
}