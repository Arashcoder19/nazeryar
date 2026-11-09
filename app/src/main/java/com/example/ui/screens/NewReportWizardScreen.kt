package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import com.example.data.model.ReportStage
import com.example.ui.screens.report_wizard.EditableChecklistItem
import com.example.ui.screens.report_wizard.ReportStepChecklistDescription
import com.example.ui.screens.report_wizard.ReportStepPhotosGps
import com.example.ui.screens.report_wizard.ReportStepPlanCompliance
import com.example.ui.screens.report_wizard.ReportStepSafety
import com.example.ui.screens.report_wizard.ReportStepStageDate
import com.example.ui.viewmodel.NazerYarViewModel
import com.example.util.ConfigManager
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportWizardScreen(
  projectId: String,
  initialStageId: String?,
  viewModel: NazerYarViewModel,
  onCancel: () -> Unit,
  onCompleted: (Report) -> Unit
) {
  val context = LocalContext.current
  val projectFlow = remember(projectId) { viewModel.getProjectFlow(projectId) }
  val project by projectFlow.collectAsState(initial = null)

  val stagesFlow = remember(projectId) { viewModel.getStagesFlow(projectId) }
  val stages by stagesFlow.collectAsState(initial = emptyList())

  val maxPhotosLimit by viewModel.maxPhotosSetting.collectAsState()

  var currentStep by remember { mutableIntStateOf(1) }

  // Step 1: Stage & Date
  var selectedStage by remember { mutableStateOf<ReportStage?>(null) }
  val reportDate = remember { System.currentTimeMillis() }

  LaunchedEffect(stages, initialStageId) {
    if (selectedStage == null && stages.isNotEmpty()) {
      selectedStage = if (initialStageId != null) {
        stages.find { it.id == initialStageId } ?: stages.first()
      } else {
        stages.first()
      }
    }
  }

  // Step 2: Safety
  var safetyCompliance by remember { mutableStateOf(true) }
  var safetyNonComplianceNote by remember { mutableStateOf("") }

  // Step 3: Plan Compliance
  var matchesApprovedPlan by remember { mutableStateOf(true) }
  var discrepancyNote by remember { mutableStateOf("") }

  // Step 4: Checklist & Description
  val checklistItems = remember { mutableStateListOf<EditableChecklistItem>() }
  var reportDescription by remember { mutableStateOf("") }

  LaunchedEffect(selectedStage) {
    val stage = selectedStage ?: return@LaunchedEffect
    val curProject = project ?: return@LaunchedEffect
    checklistItems.clear()
    val defaults = ConfigManager.getDefaultChecklistForStage(context, curProject.buildingGroup, stage.stageNumber)
    defaults.forEach { text ->
      checklistItems.add(EditableChecklistItem(itemText = text, isChecked = true, isCustom = false))
    }
  }

  // Step 5: Photos & GPS
  val photoPaths = remember { mutableStateListOf<String>() }
  var gpsLat by remember { mutableStateOf<Double?>(null) }
  var gpsLng by remember { mutableStateOf<Double?>(null) }
  var markStageCompleted by remember { mutableStateOf(true) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetMultipleContents()
  ) { uris ->
    uris.take(maxPhotosLimit - photoPaths.size).forEach { uri ->
      runCatching {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "report_photo_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        photoPaths.add(file.absolutePath)
      }
    }
  }

  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (granted) {
      fetchCurrentLocation(context) { lat, lng ->
        gpsLat = lat
        gpsLng = lng
      }
    }
  }

  BackHandler {
    if (currentStep > 1) currentStep-- else onCancel()
  }

  if (project == null || selectedStage == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("در حال آماده‌سازی...")
    }
    return
  }

  val curProject = project!!
  val curStage = selectedStage!!

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "ثبت گزارش رسمی (مرحله $currentStep از ۵)",
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = {
            if (currentStep > 1) currentStep-- else onCancel()
          }) {
            Icon(Icons.Default.Close, contentDescription = "انصراف")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primary,
          titleContentColor = Color.White,
          navigationIconContentColor = Color.White
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // Step indicator row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf("انتخاب مرحله", "بخش الف: ایمنی", "بخش ب: انطباق", "بخش ج و د: چک‌لیست", "عکس و GPS").forEachIndexed { idx, stepName ->
          val stepNum = idx + 1
          val isActive = currentStep >= stepNum
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isActive) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stepNum.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = stepName,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray,
              maxLines = 1
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      when (currentStep) {
        1 -> ReportStepStageDate(
          project = curProject,
          stages = stages,
          selectedStage = curStage,
          reportDate = reportDate,
          onSelectStage = { selectedStage = it }
        )

        2 -> ReportStepSafety(
          safetyCompliance = safetyCompliance,
          safetyNonComplianceNote = safetyNonComplianceNote,
          onSafetyChange = { safetyCompliance = it },
          onNoteChange = { safetyNonComplianceNote = it }
        )

        3 -> ReportStepPlanCompliance(
          matchesApprovedPlan = matchesApprovedPlan,
          discrepancyNote = discrepancyNote,
          onPlanChange = { matchesApprovedPlan = it },
          onNoteChange = { discrepancyNote = it }
        )

        4 -> ReportStepChecklistDescription(
          stageNumber = curStage.stageNumber,
          checklistItems = checklistItems,
          reportDescription = reportDescription,
          onDescriptionChange = { reportDescription = it }
        )

        5 -> ReportStepPhotosGps(
          photoPaths = photoPaths,
          maxPhotosLimit = maxPhotosLimit,
          gpsLat = gpsLat,
          gpsLng = gpsLng,
          markStageCompleted = markStageCompleted,
          onRequestLocation = {
            val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            if (fine == PackageManager.PERMISSION_GRANTED) {
              fetchCurrentLocation(context) { lat, lng ->
                gpsLat = lat
                gpsLng = lng
              }
            } else {
              locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
              )
            }
          },
          onPickPhotos = { photoPickerLauncher.launch("image/*") },
          onToggleMarkCompleted = { markStageCompleted = it }
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        if (currentStep > 1) {
          OutlinedButton(
            onClick = { currentStep-- },
            modifier = Modifier.testTag("report_wizard_prev_button")
          ) {
            Text("مرحله قبل")
          }
        } else {
          Spacer(modifier = Modifier.width(8.dp))
        }

        if (currentStep < 5) {
          Button(
            onClick = { currentStep++ },
            modifier = Modifier.testTag("report_wizard_next_button")
          ) {
            Text("مرحله بعد")
          }
        } else {
          Button(
            onClick = {
              val reportId = UUID.randomUUID().toString()
              val newReport = Report(
                id = reportId,
                projectId = curProject.id,
                stageId = curStage.id,
                stageNumber = curStage.stageNumber,
                reportDate = reportDate,
                safetyCompliance = safetyCompliance,
                safetyNonComplianceNote = if (!safetyCompliance) safetyNonComplianceNote.trim() else null,
                matchesApprovedPlan = matchesApprovedPlan,
                discrepancyNote = if (!matchesApprovedPlan) discrepancyNote.trim() else null,
                stageReportTypeChecked = curStage.stageTitle,
                description = reportDescription.trim(),
                gpsLat = gpsLat,
                gpsLng = gpsLng
              )

              val checklistRecords = checklistItems.mapIndexed { index, item ->
                ReportChecklistItem(
                  id = UUID.randomUUID().toString(),
                  reportId = reportId,
                  itemText = item.itemText,
                  isChecked = item.isChecked,
                  sortOrder = index,
                  isCustom = item.isCustom
                )
              }

              val photoRecords = photoPaths.mapIndexed { index, path ->
                ReportPhoto(
                  id = UUID.randomUUID().toString(),
                  reportId = reportId,
                  filePath = path,
                  sortOrder = index
                )
              }

              viewModel.createReport(
                report = newReport,
                checklistItems = checklistRecords,
                photos = photoRecords,
                markStageCompleted = markStageCompleted,
                onSuccess = { createdReport ->
                  onCompleted(createdReport)
                }
              )
            },
            modifier = Modifier.testTag("report_wizard_submit_button")
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("✓ ثبت نهایی گزارش")
          }
        }
      }
    }
  }
}

@SuppressLint("MissingPermission")
private fun fetchCurrentLocation(context: Context, onResult: (Double, Double) -> Unit) {
  val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
  fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
    if (loc != null) {
      onResult(loc.latitude, loc.longitude)
    } else {
      onResult(35.6892, 51.3890)
    }
  }.addOnFailureListener {
    onResult(35.6892, 51.3890)
  }
}
