package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EngineerDiscipline
import com.example.data.model.EngineerGrade
import com.example.data.model.UserProfile
import com.example.ui.theme.ConstructionAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
  existingProfile: UserProfile?,
  isFirstLaunch: Boolean,
  onSave: (
    firstName: String,
    lastName: String,
    membershipNumber: String,
    licenseNumber: String,
    grade: EngineerGrade,
    discipline: EngineerDiscipline
  ) -> Unit
) {
  var firstName by remember { mutableStateOf(existingProfile?.firstName ?: "") }
  var lastName by remember { mutableStateOf(existingProfile?.lastName ?: "") }
  var membershipNumber by remember { mutableStateOf(existingProfile?.membershipNumber ?: "") }
  var licenseNumber by remember { mutableStateOf(existingProfile?.licenseNumber ?: "") }
  var selectedGrade by remember { mutableStateOf(existingProfile?.grade ?: EngineerGrade.GRADE_2) }
  var selectedDiscipline by remember { mutableStateOf(existingProfile?.discipline ?: EngineerDiscipline.CIVIL) }

  var gradeExpanded by remember { mutableStateOf(false) }
  var disciplineExpanded by remember { mutableStateOf(false) }

  val canSave = firstName.isNotBlank() &&
    lastName.isNotBlank() &&
    membershipNumber.isNotBlank() &&
    licenseNumber.isNotBlank()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isFirstLaunch) "ثبت مشخصات مهندس ناظر" else "ویرایش پروفایل مهندسی",
            fontWeight = FontWeight.Bold
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primary,
          titleContentColor = Color.White
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
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (isFirstLaunch) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Engineering,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "به سامانه نظریار خوش آمدید",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = "جهت صدور گزارش‌های رسمی ماده ۱۳، لطفاً مشخصات پروانه اشتغال خود را یک‌بار ثبت نمایید.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Name Inputs
      Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = firstName,
          onValueChange = { firstName = it },
          label = { Text("نام") },
          modifier = Modifier
            .weight(1f)
            .testTag("first_name_input"),
          singleLine = true
        )
        Spacer(modifier = Modifier.width(10.dp))
        OutlinedTextField(
          value = lastName,
          onValueChange = { lastName = it },
          label = { Text("نام خانوادگی") },
          modifier = Modifier
            .weight(1f)
            .testTag("last_name_input"),
          singleLine = true
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Numbers
      OutlinedTextField(
        value = membershipNumber,
        onValueChange = { membershipNumber = it },
        label = { Text("شماره عضویت سازمان نظام مهندسی") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("membership_number_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = licenseNumber,
        onValueChange = { licenseNumber = it },
        label = { Text("شماره پروانه اشتغال به کار مهندسی") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("license_number_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Grade Dropdown
      ExposedDropdownMenuBox(
        expanded = gradeExpanded,
        onExpandedChange = { gradeExpanded = !gradeExpanded },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = selectedGrade.titleFa,
          onValueChange = {},
          readOnly = true,
          label = { Text("پایه مهندسی") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
            .testTag("grade_dropdown")
        )
        ExposedDropdownMenu(
          expanded = gradeExpanded,
          onDismissRequest = { gradeExpanded = false }
        ) {
          EngineerGrade.entries.forEach { grade ->
            DropdownMenuItem(
              text = { Text(grade.titleFa) },
              onClick = {
                selectedGrade = grade
                gradeExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Discipline Dropdown
      ExposedDropdownMenuBox(
        expanded = disciplineExpanded,
        onExpandedChange = { disciplineExpanded = !disciplineExpanded },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = selectedDiscipline.titleFa,
          onValueChange = {},
          readOnly = true,
          label = { Text("رشته تخصصی مهندسی") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = disciplineExpanded) },
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
            .testTag("discipline_dropdown")
        )
        ExposedDropdownMenu(
          expanded = disciplineExpanded,
          onDismissRequest = { disciplineExpanded = false }
        ) {
          EngineerDiscipline.entries.forEach { disc ->
            DropdownMenuItem(
              text = { Text(disc.titleFa) },
              onClick = {
                selectedDiscipline = disc
                disciplineExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      Button(
        onClick = {
          onSave(
            firstName,
            lastName,
            membershipNumber,
            licenseNumber,
            selectedGrade,
            selectedDiscipline
          )
        },
        enabled = canSave,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("save_profile_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Check, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isFirstLaunch) "تأیید و ورود به برنامه" else "ذخیره تغییرات",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
