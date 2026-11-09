package com.example.ui.screens.report_wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReportStepChecklistDescription(
  stageNumber: Int,
  checklistItems: SnapshotStateList<EditableChecklistItem>,
  reportDescription: String,
  onDescriptionChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var newCustomItemText by remember { mutableStateOf("") }

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "بخش ج: چک‌لیست کنترل مرحله $stageNumber",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "آیتم‌های علامت‌خورده در صفحه اول فرم چاپی با حروف برجسته ثبت می‌شوند.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(12.dp))

    checklistItems.forEachIndexed { idx, item ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = item.isChecked,
          onCheckedChange = { checked ->
            checklistItems[idx] = item.copy(isChecked = checked)
          }
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = item.itemText,
          style = MaterialTheme.typography.bodySmall,
          fontWeight = if (item.isChecked) FontWeight.Bold else FontWeight.Normal,
          modifier = Modifier.weight(1f)
        )
        if (item.isCustom) {
          IconButton(onClick = { checklistItems.removeAt(idx) }) {
            Icon(Icons.Default.Close, contentDescription = "حذف", modifier = Modifier.size(16.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Add custom checklist item
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = newCustomItemText,
        onValueChange = { newCustomItemText = it },
        label = { Text("افزودن بند سفارشی به چک‌لیست") },
        modifier = Modifier.weight(1f),
        singleLine = true
      )
      Spacer(modifier = Modifier.width(8.dp))
      Button(
        onClick = {
          if (newCustomItemText.isNotBlank()) {
            checklistItems.add(EditableChecklistItem(itemText = newCustomItemText.trim(), isChecked = true, isCustom = true))
            newCustomItemText = ""
          }
        },
        enabled = newCustomItemText.isNotBlank()
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Section D: Free Description
    Text(
      text = "بخش د: شرح آزاد گزارش و دستور کارها",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
      value = reportDescription,
      onValueChange = onDescriptionChange,
      label = { Text("توضیحات تکمیلی، مشاهدات و دستور کارهای صادره به مجری...") },
      modifier = Modifier.fillMaxWidth(),
      minLines = 4
    )
  }
}
