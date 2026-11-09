package com.example.ui.screens.project_wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProjectStepBaseInfo(
  title: String,
  onTitleChange: (String) -> Unit,
  province: String,
  onProvinceChange: (String) -> Unit,
  city: String,
  onCityChange: (String) -> Unit,
  municipalityZone: String,
  onZoneChange: (String) -> Unit,
  street: String,
  onStreetChange: (String) -> Unit,
  alley: String,
  onAlleyChange: (String) -> Unit,
  plateNumber: String,
  onPlateChange: (String) -> Unit,
  registeredPlateNumber: String,
  onRegisteredPlateChange: (String) -> Unit,
  buildingPermitNumber: String,
  onPermitChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "مشخصات ثبتی و نشانی ملک",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = title,
      onValueChange = onTitleChange,
      label = { Text("عنوان پروژه (مثال: ساختمان مسکونی سعادت‌آباد)") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("project_title_input"),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
      OutlinedTextField(
        value = province,
        onValueChange = onProvinceChange,
        label = { Text("استان") },
        modifier = Modifier
          .weight(1f)
          .testTag("province_input"),
        singleLine = true
      )
      Spacer(modifier = Modifier.width(8.dp))
      OutlinedTextField(
        value = city,
        onValueChange = onCityChange,
        label = { Text("شهر") },
        modifier = Modifier
          .weight(1f)
          .testTag("city_input"),
        singleLine = true
      )
      Spacer(modifier = Modifier.width(8.dp))
      OutlinedTextField(
        value = municipalityZone,
        onValueChange = onZoneChange,
        label = { Text("منطقه") },
        modifier = Modifier
          .weight(0.8f)
          .testTag("zone_input"),
        singleLine = true
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
      OutlinedTextField(
        value = street,
        onValueChange = onStreetChange,
        label = { Text("خیابان") },
        modifier = Modifier.weight(1.5f),
        singleLine = true
      )
      Spacer(modifier = Modifier.width(8.dp))
      OutlinedTextField(
        value = alley,
        onValueChange = onAlleyChange,
        label = { Text("کوچه") },
        modifier = Modifier.weight(1f),
        singleLine = true
      )
      Spacer(modifier = Modifier.width(8.dp))
      OutlinedTextField(
        value = plateNumber,
        onValueChange = onPlateChange,
        label = { Text("پلاک") },
        modifier = Modifier.weight(0.8f),
        singleLine = true
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = registeredPlateNumber,
      onValueChange = onRegisteredPlateChange,
      label = { Text("شماره پلاک ثبتی (ضروری برای فرم رسمی)") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("registered_plate_input"),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = buildingPermitNumber,
      onValueChange = onPermitChange,
      label = { Text("شماره پروانه ساختمانی شهرداری") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("permit_number_input"),
      singleLine = true
    )
  }
}
