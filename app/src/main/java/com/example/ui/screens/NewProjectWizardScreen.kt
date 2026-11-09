package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuildingGroup
import com.example.data.model.Project
import com.example.data.model.ProjectLifecycleStatus
import com.example.data.model.ProjectMember
import com.example.data.model.UserProfile
import com.example.ui.screens.project_wizard.ProjectStepBaseInfo
import com.example.ui.screens.project_wizard.ProjectStepBuildingGroup
import com.example.ui.screens.project_wizard.ProjectStepMembers
import com.example.ui.screens.project_wizard.ProjectStepSummary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProjectWizardScreen(
  userProfile: UserProfile?,
  onCancel: () -> Unit,
  onComplete: (project: Project, members: List<ProjectMember>) -> Unit
) {
  var currentStep by remember { mutableIntStateOf(1) }

  // Step 1: Base Info
  var title by remember { mutableStateOf("") }
  var province by remember { mutableStateOf("تهران") }
  var city by remember { mutableStateOf("تهران") }
  var municipalityZone by remember { mutableStateOf("") }
  var street by remember { mutableStateOf("") }
  var alley by remember { mutableStateOf("") }
  var plateNumber by remember { mutableStateOf("") }
  var registeredPlateNumber by remember { mutableStateOf("") }
  var buildingPermitNumber by remember { mutableStateOf("") }

  // Step 2: Building Group
  var buildingGroup by remember { mutableStateOf(BuildingGroup.AB) }

  // Step 3: Members
  var ownerName by remember { mutableStateOf("") }
  var ownerPhone by remember { mutableStateOf("") }
  var contractorName by remember { mutableStateOf("") }
  var contractorPhone by remember { mutableStateOf("") }
  var isUserCoordinatingSupervisor by remember { mutableStateOf(true) }

  BackHandler {
    if (currentStep > 1) {
      currentStep--
    } else {
      onCancel()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "تعریف پروژه جدید (مرحله $currentStep از ۴)",
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = {
            if (currentStep > 1) currentStep-- else onCancel()
          }) {
            Icon(Icons.Default.Close, contentDescription = "انصراف")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primary,
          titleContentColor = Color.White,
          navigationIconContentColor = Color.White
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // Step indicator row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf("مشخصات پایه", "گروه ساختمانی", "اعضای پروژه", "تأیید نهایی").forEachIndexed { idx, stepName ->
          val stepNum = idx + 1
          val isActive = currentStep >= stepNum
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isActive) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stepNum.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = stepName,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray,
              maxLines = 1
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      when (currentStep) {
        1 -> ProjectStepBaseInfo(
          title = title,
          onTitleChange = { title = it },
          province = province,
          onProvinceChange = { province = it },
          city = city,
          onCityChange = { city = it },
          municipalityZone = municipalityZone,
          onZoneChange = { municipalityZone = it },
          street = street,
          onStreetChange = { street = it },
          alley = alley,
          onAlleyChange = { alley = it },
          plateNumber = plateNumber,
          onPlateChange = { plateNumber = it },
          registeredPlateNumber = registeredPlateNumber,
          onRegisteredPlateChange = { registeredPlateNumber = it },
          buildingPermitNumber = buildingPermitNumber,
          onPermitChange = { buildingPermitNumber = it }
        )

        2 -> ProjectStepBuildingGroup(
          buildingGroup = buildingGroup,
          onGroupChange = { buildingGroup = it }
        )

        3 -> ProjectStepMembers(
          ownerName = ownerName,
          onOwnerNameChange = { ownerName = it },
          ownerPhone = ownerPhone,
          onOwnerPhoneChange = { ownerPhone = it },
          contractorName = contractorName,
          onContractorNameChange = { contractorName = it },
          contractorPhone = contractorPhone,
          onContractorPhoneChange = { contractorPhone = it },
          isUserCoordinatingSupervisor = isUserCoordinatingSupervisor,
          onCoordSupervisorChange = { isUserCoordinatingSupervisor = it }
        )

        4 -> ProjectStepSummary(
          title = title,
          registeredPlateNumber = registeredPlateNumber,
          buildingPermitNumber = buildingPermitNumber,
          buildingGroup = buildingGroup,
          province = province,
          city = city,
          street = street,
          ownerName = ownerName,
          ownerPhone = ownerPhone
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Navigation Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        if (currentStep > 1) {
          OutlinedButton(
            onClick = { currentStep-- },
            modifier = Modifier.testTag("wizard_prev_button")
          ) {
            Text("مرحله قبل")
          }
        } else {
          Spacer(modifier = Modifier.width(8.dp))
        }

        if (currentStep < 4) {
          val canGoNext = when (currentStep) {
            1 -> title.isNotBlank() && registeredPlateNumber.isNotBlank()
            else -> true
          }
          Button(
            onClick = { currentStep++ },
            enabled = canGoNext,
            modifier = Modifier.testTag("wizard_next_button")
          ) {
            Text("مرحله بعد")
          }
        } else {
          Button(
            onClick = {
              val projectId = UUID.randomUUID().toString()
              val project = Project(
                id = projectId,
                title = title.trim(),
                province = province.trim(),
                city = city.trim(),
                municipalityZone = municipalityZone.trim(),
                street = street.trim(),
                alley = alley.trim(),
                plateNumber = plateNumber.trim(),
                registeredPlateNumber = registeredPlateNumber.trim(),
                buildingPermitNumber = buildingPermitNumber.trim(),
                buildingGroup = buildingGroup,
                lifecycleStatus = ProjectLifecycleStatus.ACTIVE
              )

              val membersList = mutableListOf<ProjectMember>()

              if (userProfile != null) {
                membersList.add(
                  ProjectMember(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    fullName = userProfile.fullName,
                    role = "مهندس ناظر ${userProfile.discipline.titleFa}",
                    phoneNumber = null,
                    isSupervisor = true,
                    isCoordinatingSupervisor = isUserCoordinatingSupervisor,
                    isAppUser = true,
                    sortOrder = 0
                  )
                )
              }

              if (ownerName.isNotBlank()) {
                membersList.add(
                  ProjectMember(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    fullName = ownerName.trim(),
                    role = "مالک / صاحب‌کار",
                    phoneNumber = ownerPhone.trim().ifBlank { null },
                    isSupervisor = false,
                    isCoordinatingSupervisor = false,
                    isAppUser = false,
                    sortOrder = 1
                  )
                )
              }

              if (contractorName.isNotBlank()) {
                membersList.add(
                  ProjectMember(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    fullName = contractorName.trim(),
                    role = "مجری / پیمانکار",
                    phoneNumber = contractorPhone.trim().ifBlank { null },
                    isSupervisor = false,
                    isCoordinatingSupervisor = false,
                    isAppUser = false,
                    sortOrder = 2
                  )
                )
              }

              onComplete(project, membersList)
            },
            modifier = Modifier.testTag("wizard_complete_button")
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("✓ ساخت پروژه")
          }
        }
      }
    }
  }
}
