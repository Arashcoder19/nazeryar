package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.Project
import com.example.data.model.Report
import com.example.ui.theme.ConstructionAmber
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyRed
import com.example.ui.viewmodel.NazerYarViewModel
import com.example.util.DateUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewReportDetailScreen(
  projectId: String,
  reportId: String,
  viewModel: NazerYarViewModel,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val projectFlow = remember(projectId) { viewModel.getProjectFlow(projectId) }
  val project by projectFlow.collectAsState(initial = null)

  val reportsFlow = remember(projectId) { viewModel.getReportsFlow(projectId) }
  val reports by reportsFlow.collectAsState(initial = emptyList())
  val report = reports.find { it.id == reportId }

  val checklistFlow = remember(reportId) { viewModel.getChecklistForReportFlow(reportId) }
  val checklist by checklistFlow.collectAsState(initial = emptyList())

  val photosFlow = remember(reportId) { viewModel.getPhotosForReportFlow(reportId) }
  val photos by photosFlow.collectAsState(initial = emptyList())

  var showPdfDialog by remember { mutableStateOf(false) }
  var includePdfAttachmentPage by remember { mutableStateOf(true) }
  var showDeleteDialog by remember { mutableStateOf(false) }

  BackHandler { onBack() }

  if (project == null || report == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("در حال بارگذاری جزئیات...")
    }
    return
  }

  val curProject = project!!
  val curReport = report

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "گزارش مرحله ${curReport.stageNumber}",
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
          }
        },
        actions = {
          IconButton(onClick = { showPdfDialog = true }) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "دریافت PDF")
          }
          IconButton(onClick = { showDeleteDialog = true }) {
            Icon(Icons.Default.Delete, contentDescription = "حذف گزارش")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primary,
          titleContentColor = Color.White,
          navigationIconContentColor = Color.White,
          actionIconContentColor = Color.White
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Main Metadata Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "پروژه: " + curProject.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "تاریخ گزارش: " + DateUtils.formatToPersianDate(curReport.reportDate) + " (" + DateUtils.formatToPersianDateNumeric(curReport.reportDate) + ")",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      // Section A: Safety
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "الف) ایمنی کارگاه و حفاظت فنی:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (curReport.safetyCompliance) "رعایت شده است ✓" else "رعایت نشده است ✕",
            color = if (curReport.safetyCompliance) SafetyGreen else SafetyRed,
            fontWeight = FontWeight.Bold
          )
          if (!curReport.safetyCompliance && !curReport.safetyNonComplianceNote.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "شرح عدم رعایت: " + curReport.safetyNonComplianceNote, style = MaterialTheme.typography.bodySmall)
          }
        }
      }

      // Section B: Plan Compliance
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "ب) انطباق با نقشه‌های مصوب:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (curReport.matchesApprovedPlan) "منطبق با نقشه‌های مصوب است ✓" else "دارای مغایرت با نقشه‌ها ✕",
            color = if (curReport.matchesApprovedPlan) SafetyGreen else SafetyRed,
            fontWeight = FontWeight.Bold
          )
          if (!curReport.matchesApprovedPlan && !curReport.discrepancyNote.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "شرح مغایرت: " + curReport.discrepancyNote, style = MaterialTheme.typography.bodySmall)
          }
        }
      }

      // Section C: Checklist
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "ج) چک‌لیست کنترل مرحله:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Spacer(modifier = Modifier.height(8.dp))
          checklist.forEach { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (item.isChecked) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (item.isChecked) SafetyGreen else Color.Gray,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = item.itemText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (item.isChecked) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }

      // Section D: Free Description
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "د) شرح گزارش و دستور کارها:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (curReport.description.isNotBlank()) curReport.description else "بدون شرح اضافی.",
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }

      // Photos & GPS
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "تصاویر و موقعیت مکانی (ضمیمه):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Spacer(modifier = Modifier.height(8.dp))

          if (curReport.gpsLat != null && curReport.gpsLng != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${curReport.gpsLat} , ${curReport.gpsLng}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.width(8.dp))
              TextButton(onClick = {
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${curReport.gpsLat},${curReport.gpsLng}?q=${curReport.gpsLat},${curReport.gpsLng}"))
                runCatching { context.startActivity(mapIntent) }
              }) {
                Text("نمایش در نقشه", fontSize = 11.sp)
              }
            }
          }

          if (photos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              photos.forEach { p ->
                Image(
                  painter = rememberAsyncImagePainter(File(p.filePath)),
                  contentDescription = null,
                  modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                  contentScale = ContentScale.Crop
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Button: Get Official PDF
      Button(
        onClick = { showPdfDialog = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("تولید و دریافت PDF رسمی (ماده ۱۳)", fontWeight = FontWeight.Bold)
      }
    }
  }

  // PDF Dialog
  if (showPdfDialog) {
    AlertDialog(
      onDismissRequest = { showPdfDialog = false },
      title = { Text("خروجی PDF رسمی گزارش مرحله ${curReport.stageNumber}", fontWeight = FontWeight.Bold) },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "صفحه ۱ (فرم اداری ماده ۱۳ آیین‌نامه اجرایی ماده ۳۳) همیشه مطابق قالب قانونی شهرداری و نظام مهندسی تولید می‌گردد.",
            style = MaterialTheme.typography.bodySmall
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = includePdfAttachmentPage,
              onCheckedChange = { includePdfAttachmentPage = it }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "افزودن صفحه دوم (ضمیمه عکس و GPS)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "اختیاری - دارای نشانه «ضمیمه داخلی، جزو فرم رسمی نیست»",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showPdfDialog = false
            viewModel.generatePdf(curProject, curReport, includePdfAttachmentPage) { file ->
              if (file != null) {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                  setDataAndType(uri, "application/pdf")
                  addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                runCatching {
                  context.startActivity(Intent.createChooser(intent, "مشاهده / ارسال فایل PDF گزارش"))
                }
              }
            }
          }
        ) {
          Text("تولید و دریافت PDF")
        }
      },
      dismissButton = {
        TextButton(onClick = { showPdfDialog = false }) {
          Text("بستن")
        }
      }
    )
  }

  // Delete Dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text("حذف گزارش", fontWeight = FontWeight.Bold, color = SafetyRed) },
      text = { Text("آیا از حذف این گزارش اطمینان دارید؟") },
      confirmButton = {
        Button(
          onClick = {
            showDeleteDialog = false
            viewModel.deleteReport(curReport.id)
            onBack()
          },
          colors = ButtonDefaults.buttonColors(containerColor = SafetyRed)
        ) {
          Text("حذف")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("انصراف")
        }
      }
    )
  }
}
