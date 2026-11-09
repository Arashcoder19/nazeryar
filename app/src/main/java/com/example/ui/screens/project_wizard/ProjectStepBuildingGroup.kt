package com.example.ui.screens.project_wizard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.BuildingGroup

@Composable
fun ProjectStepBuildingGroup(
  buildingGroup: BuildingGroup,
  onGroupChange: (BuildingGroup) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "انتخاب گروه ساختمانی",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "تعداد مراحل گزارش رسمی بر اساس ماده ۱۳ آیین‌نامه اجرایی تعیین می‌گردد.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Option AB
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = if (buildingGroup == BuildingGroup.AB) 2.dp else 1.dp,
          color = if (buildingGroup == BuildingGroup.AB) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1),
          shape = RoundedCornerShape(12.dp)
        )
        .clickable { onGroupChange(BuildingGroup.AB) }
        .testTag("group_ab_card"),
      colors = CardDefaults.cardColors(
        containerColor = if (buildingGroup == BuildingGroup.AB) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        RadioButton(
          selected = buildingGroup == BuildingGroup.AB,
          onClick = { onGroupChange(BuildingGroup.AB) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "گروه الف و ب (حداقل ۱۰ مرحله)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "مناسب برای ساختمان‌های تا ۵ طبقه یا با سطح اشغال معمول. مراحل نظارت از همجواری تا پایان کار.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Option CD
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = if (buildingGroup == BuildingGroup.CD) 2.dp else 1.dp,
          color = if (buildingGroup == BuildingGroup.CD) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1),
          shape = RoundedCornerShape(12.dp)
        )
        .clickable { onGroupChange(BuildingGroup.CD) }
        .testTag("group_cd_card"),
      colors = CardDefaults.cardColors(
        containerColor = if (buildingGroup == BuildingGroup.CD) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        RadioButton(
          selected = buildingGroup == BuildingGroup.CD,
          onClick = { onGroupChange(BuildingGroup.CD) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "گروه ج و د (حداقل ۱۴ مرحله)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "ساختمان‌های بلندمرتبه و پیچیده با طبقات زیرزمین، نیازمند کنترل سقف‌های منفی، طبقات میانی و تجهیزات جامع.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
