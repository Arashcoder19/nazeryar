package com.example.ui.screens.project_wizard

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProjectStepMembers(
  ownerName: String,
  onOwnerNameChange: (String) -> Unit,
  ownerPhone: String,
  onOwnerPhoneChange: (String) -> Unit,
  contractorName: String,
  onContractorNameChange: (String) -> Unit,
  contractorPhone: String,
  onContractorPhoneChange: (String) -> Unit,
  isUserCoordinatingSupervisor: Boolean,
  onCoordSupervisorChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "اعضای کلیدی پروژه (اختیاری)",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "می‌توانید اطلاعات تماس مالک و مجری/پیمانکار را اکنون یا بعداً ثبت نمایید.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Coordinating Supervisor Checkbox
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(10.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onCoordSupervisorChange(!isUserCoordinatingSupervisor) }
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = isUserCoordinatingSupervisor,
          onCheckedChange = onCoordSupervisorChange,
          modifier = Modifier.testTag("coordinating_supervisor_checkbox")
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "اینجانب ناظر هماهنگ‌کننده این پروژه هستم",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "هماهنگی بین ناظرین سازه، معماری، برق و مکانیک بر عهده ناظر هماهنگ‌کننده است.",
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = ownerName,
      onValueChange = onOwnerNameChange,
      label = { Text("نام مالک یا صاحب‌کار") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = ownerPhone,
      onValueChange = onOwnerPhoneChange,
      label = { Text("شماره تماس مالک") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = contractorName,
      onValueChange = onContractorNameChange,
      label = { Text("نام پیمانکار یا مجری ذیصلاح") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = contractorPhone,
      onValueChange = onContractorPhoneChange,
      label = { Text("شماره تماس مجری/پیمانکار") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true
    )
  }
}
