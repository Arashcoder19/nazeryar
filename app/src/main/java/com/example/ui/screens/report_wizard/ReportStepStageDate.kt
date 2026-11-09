package com.example.ui.screens.report_wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Project
import com.example.data.model.ReportStage
import com.example.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportStepStageDate(
  project: Project,
  stages: List<ReportStage>,
  selectedStage: ReportStage,
  reportDate: Long,
  onSelectStage: (ReportStage) -> Unit,
  modifier: Modifier = Modifier
) {
  var stageDropdownExpanded by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "اطلاعات مرحله نظارت و تاریخ گزارش",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "پروژه: ${project.title} (${project.buildingGroup.titleFa})",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(14.dp))

    ExposedDropdownMenuBox(
      expanded = stageDropdownExpanded,
      onExpandedChange = { stageDropdownExpanded = !stageDropdownExpanded },
      modifier = Modifier.fillMaxWidth()
    ) {
      OutlinedTextField(
        value = "مرحله ${selectedStage.stageNumber}: ${selectedStage.stageTitle}",
        onValueChange = {},
        readOnly = true,
        label = { Text("مرحله گزارش نظارتی") },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageDropdownExpanded) },
        modifier = Modifier
          .menuAnchor()
          .fillMaxWidth()
      )
      ExposedDropdownMenu(
        expanded = stageDropdownExpanded,
        onDismissRequest = { stageDropdownExpanded = false }
      ) {
        stages.forEach { st ->
          DropdownMenuItem(
            text = {
              Text(
                text = "مرحله ${st.stageNumber}: ${st.stageTitle}",
                fontWeight = if (st.id == selectedStage.id) FontWeight.Bold else FontWeight.Normal
              )
            },
            onClick = {
              onSelectStage(st)
              stageDropdownExpanded = false
            }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = "تاریخ رسمی تنظیم گزارش:",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = DateUtils.formatToPersianDate(reportDate) + " (" + DateUtils.formatToPersianDateNumeric(reportDate) + ")",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "شماره گزارش معادل شماره مرحله (${selectedStage.stageNumber}) در فرم چاپی درج می‌شود.",
          style = MaterialTheme.typography.bodySmall
        )
      }
    }
  }
}
