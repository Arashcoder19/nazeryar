package com.example.ui.screens.project_wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.BuildingGroup
import com.example.ui.theme.SafetyGreen

@Composable
fun ProjectStepSummary(
  title: String,
  registeredPlateNumber: String,
  buildingPermitNumber: String,
  buildingGroup: BuildingGroup,
  province: String,
  city: String,
  street: String,
  ownerName: String,
  ownerPhone: String,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "خلاصه اطلاعات و تأیید نهایی",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(12.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(12.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "عنوان: $title",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "پلاک ثبتی: $registeredPlateNumber",
          style = MaterialTheme.typography.bodyMedium
        )
        Text(
          text = "شماره پروانه: $buildingPermitNumber",
          style = MaterialTheme.typography.bodyMedium
        )
        Text(
          text = "گروه: ${buildingGroup.titleFa}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "نشانی: $province، $city، خیابان $street",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (ownerName.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "مالک: $ownerName ($ownerPhone)",
            style = MaterialTheme.typography.bodySmall
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "با تأیید، ${buildingGroup.stageCount} مرحله گزارش نظارتی بر اساس الگوی رسمی سازمان نظام مهندسی در دیتابیس ساخته می‌شود.",
          style = MaterialTheme.typography.bodySmall,
          color = SafetyGreen
        )
      }
    }
  }
}
