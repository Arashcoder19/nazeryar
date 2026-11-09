package com.example.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Report
import com.example.data.model.ReportStage
import com.example.ui.components.StatutoryWarningBanner
import com.example.ui.theme.SafetyGreen
import com.example.util.DateUtils

@Composable
fun ReportsTabContent(
  stages: List<ReportStage>,
  reports: List<Report>,
  onNewReport: (stageId: String) -> Unit,
  onViewReport: (reportId: String) -> Unit,
  onExportPdf: (Report) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      StatutoryWarningBanner()
    }

    items(stages, key = { it.id }) { stage ->
      val stageReports = reports.filter { it.stageId == stage.id }
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
          containerColor = if (stage.isCompleted) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp),
        border = if (stage.isCompleted) androidx.compose.foundation.BorderStroke(1.dp, SafetyGreen.copy(alpha = 0.5f)) else null
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (stage.isCompleted) SafetyGreen else MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = DateUtils.toPersianDigits(stage.stageNumber.toString()),
                color = if (stage.isCompleted) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = stage.stageTitle,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (stageReports.isEmpty()) "هیچ گزارشی ثبت نشده" else "${stageReports.size} گزارش ثبت شده",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
              onClick = { onNewReport(stage.id) },
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("ثبت گزارش", fontSize = 12.sp)
            }
          }

          if (stageReports.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              stageReports.forEach { rep ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(
                    modifier = Modifier
                      .weight(1f)
                      .clickable { onViewReport(rep.id) }
                  ) {
                    Text(
                      text = "گزارش مورخ: " + DateUtils.formatToPersianDate(rep.reportDate),
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = (if (rep.safetyCompliance) "ایمنی: رعایت شده" else "ایمنی: عدم رعایت") + " | " +
                        (if (rep.matchesApprovedPlan) "انطباق نقشه: منطبق" else "دارای مغایرت"),
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  IconButton(
                    onClick = { onExportPdf(rep) },
                    modifier = Modifier.size(36.dp)
                  ) {
                    Icon(
                      Icons.Default.PictureAsPdf,
                      contentDescription = "دریافت PDF",
                      tint = MaterialTheme.colorScheme.primary
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
