package com.example.ui.screens.report_wizard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyRed

@Composable
fun ReportStepSafety(
  safetyCompliance: Boolean,
  safetyNonComplianceNote: String,
  onSafetyChange: (Boolean) -> Unit,
  onNoteChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "بخش الف: ایمنی کارگاه و حفاظت فنی",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(10.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(10.dp)
    ) {
      Text(
        text = "متن قانونی فرم رسمی: «رعایت اصول ایمنی و حفاظت کار و بهداشت کار در کارگاه ساختمانی مطابق مبحث ۱۲ مقررات ملی ساختمان و آیین‌نامه حفاظتی کارگاه‌های ساختمانی وزارت کار الزام‌آور است.»",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(12.dp),
        lineHeight = 20.sp
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onSafetyChange(true) }
      ) {
        RadioButton(selected = safetyCompliance, onClick = { onSafetyChange(true) })
        Text("رعایت شده است", fontWeight = FontWeight.Bold, color = SafetyGreen)
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onSafetyChange(false) }
      ) {
        RadioButton(selected = !safetyCompliance, onClick = { onSafetyChange(false) })
        Text("رعایت نشده است", fontWeight = FontWeight.Bold, color = SafetyRed)
      }
    }

    if (!safetyCompliance) {
      Spacer(modifier = Modifier.height(14.dp))
      OutlinedTextField(
        value = safetyNonComplianceNote,
        onValueChange = onNoteChange,
        label = { Text("شرح و موارد عدم رعایت ایمنی (درج در فرم رسمی)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3
      )
    }
  }
}
