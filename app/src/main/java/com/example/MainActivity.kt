package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.LicenseActivationDialog
import com.example.ui.screens.ArchiveListScreen
import com.example.ui.screens.NewProjectWizardScreen
import com.example.ui.screens.NewReportWizardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.ProjectDetailScreen
import com.example.ui.screens.ProjectsListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ViewReportDetailScreen
import com.example.ui.theme.ConstructionAmber
import com.example.ui.theme.NazerYarTheme
import com.example.ui.theme.SafetyGreen
import com.example.ui.viewmodel.NazerYarViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

  private val viewModel: NazerYarViewModel by viewModels()

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      NazerYarTheme {
        // Enforce authentic Persian RTL layout direction
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          val userProfile by viewModel.userProfile.collectAsState()
          val currentScreen by viewModel.currentScreen.collectAsState()
          val showLicenseDialog by viewModel.showLicenseDialog.collectAsState()
          val activeProjects by viewModel.activeProjects.collectAsState()
          val archivedProjects by viewModel.archivedProjects.collectAsState()

          val snackbarHostState = remember { SnackbarHostState() }
          var isEditingProfile by remember { mutableStateOf(false) }

          // Handle toast notifications
          LaunchedEffect(Unit) {
            viewModel.toastEvent.collectLatest { message ->
              Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
          }

          if (userProfile == null || isEditingProfile) {
            // First Launch / Mandatory Profile Setup
            ProfileSetupScreen(
              existingProfile = userProfile,
              isFirstLaunch = userProfile == null,
              onSave = { first, last, memberNo, licenseNo, grade, disc ->
                viewModel.saveProfile(first, last, memberNo, licenseNo, grade, disc)
                isEditingProfile = false
              }
            )
          } else {
            val isMainTab = currentScreen is Screen.ProjectsList ||
              currentScreen is Screen.ArchiveList ||
              currentScreen is Screen.Profile ||
              currentScreen is Screen.Settings

            Scaffold(
              modifier = Modifier.fillMaxSize(),
              snackbarHost = { SnackbarHost(snackbarHostState) },
              topBar = {
                if (isMainTab) {
                  TopAppBar(
                    title = {
                      Text(
                        text = when (currentScreen) {
                          is Screen.ProjectsList -> "نظریار - ناظر ساختمان"
                          is Screen.ArchiveList -> "آرشیو پروژه‌ها"
                          is Screen.Profile -> "پروفایل مهندسی"
                          is Screen.Settings -> "تنظیمات"
                          else -> "نظریار"
                        },
                        fontWeight = FontWeight.Bold
                      )
                    },
                    actions = {
                      IconButton(
                        onClick = { viewModel.openLicenseDialog() },
                        modifier = Modifier.testTag("topbar_license_button")
                      ) {
                        Icon(
                          imageVector = Icons.Default.Key,
                          contentDescription = "لایسنس",
                          tint = if (userProfile?.isActivated == true) SafetyGreen else ConstructionAmber
                        )
                      }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                      containerColor = MaterialTheme.colorScheme.primary,
                      titleContentColor = Color.White,
                      actionIconContentColor = Color.White
                    )
                  )
                }
              },
              bottomBar = {
                if (isMainTab) {
                  NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                  ) {
                    NavigationBarItem(
                      selected = currentScreen is Screen.ProjectsList,
                      onClick = { viewModel.navigateTo(Screen.ProjectsList) },
                      icon = {
                        Icon(
                          if (currentScreen is Screen.ProjectsList) Icons.Default.Apartment else Icons.Outlined.Apartment,
                          contentDescription = "پروژه‌ها"
                        )
                      },
                      label = { Text("پروژه‌ها") },
                      modifier = Modifier.testTag("nav_item_projects")
                    )

                    NavigationBarItem(
                      selected = currentScreen is Screen.ArchiveList,
                      onClick = { viewModel.navigateTo(Screen.ArchiveList) },
                      icon = {
                        Icon(
                          if (currentScreen is Screen.ArchiveList) Icons.Default.Archive else Icons.Outlined.Archive,
                          contentDescription = "آرشیو"
                        )
                      },
                      label = { Text("آرشیو") },
                      modifier = Modifier.testTag("nav_item_archive")
                    )

                    NavigationBarItem(
                      selected = currentScreen is Screen.Profile,
                      onClick = { viewModel.navigateTo(Screen.Profile) },
                      icon = {
                        Icon(
                          if (currentScreen is Screen.Profile) Icons.Default.Person else Icons.Outlined.Person,
                          contentDescription = "پروفایل"
                        )
                      },
                      label = { Text("پروفایل من") },
                      modifier = Modifier.testTag("nav_item_profile")
                    )

                    NavigationBarItem(
                      selected = currentScreen is Screen.Settings,
                      onClick = { viewModel.navigateTo(Screen.Settings) },
                      icon = {
                        Icon(
                          if (currentScreen is Screen.Settings) Icons.Default.Settings else Icons.Outlined.Settings,
                          contentDescription = "تنظیمات"
                        )
                      },
                      label = { Text("تنظیمات") },
                      modifier = Modifier.testTag("nav_item_settings")
                    )
                  }
                }
              }
            ) { innerPadding ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(innerPadding)
              ) {
                when (val screen = currentScreen) {
                  is Screen.ProjectsList -> {
                    ProjectsListScreen(
                      projects = activeProjects,
                      onProjectClick = { projectId ->
                        viewModel.navigateTo(Screen.ProjectDetail(projectId))
                      },
                      onNewProjectClick = {
                        viewModel.attemptStartNewProject {
                          viewModel.navigateTo(Screen.NewProjectWizard)
                        }
                      }
                    )
                  }

                  is Screen.ArchiveList -> {
                    ArchiveListScreen(
                      archivedProjects = archivedProjects,
                      viewModel = viewModel,
                      onProjectClick = { projectId ->
                        viewModel.navigateTo(Screen.ProjectDetail(projectId))
                      }
                    )
                  }

                  is Screen.Profile -> {
                    ProfileScreen(
                      userProfile = userProfile,
                      onEditProfile = { isEditingProfile = true },
                      onOpenLicenseDialog = { viewModel.openLicenseDialog() }
                    )
                  }

                  is Screen.Settings -> {
                    SettingsScreen(
                      viewModel = viewModel,
                      onOpenLicenseDialog = { viewModel.openLicenseDialog() }
                    )
                  }

                  is Screen.NewProjectWizard -> {
                    NewProjectWizardScreen(
                      userProfile = userProfile,
                      onCancel = { viewModel.navigateBack() },
                      onComplete = { proj, members ->
                        viewModel.createProject(proj, members) {
                          viewModel.navigateTo(Screen.ProjectsList)
                        }
                      }
                    )
                  }

                  is Screen.ProjectDetail -> {
                    ProjectDetailScreen(
                      projectId = screen.projectId,
                      viewModel = viewModel,
                      onBack = { viewModel.navigateBack() },
                      onNewReport = { stageId ->
                        viewModel.navigateTo(Screen.NewReportWizard(screen.projectId, stageId))
                      },
                      onViewReport = { reportId ->
                        viewModel.navigateTo(Screen.ViewReportDetail(screen.projectId, reportId))
                      }
                    )
                  }

                  is Screen.NewReportWizard -> {
                    NewReportWizardScreen(
                      projectId = screen.projectId,
                      initialStageId = screen.initialStageId,
                      viewModel = viewModel,
                      onCancel = { viewModel.navigateBack() },
                      onCompleted = { report ->
                        viewModel.navigateBack()
                      }
                    )
                  }

                  is Screen.ViewReportDetail -> {
                    ViewReportDetailScreen(
                      projectId = screen.projectId,
                      reportId = screen.reportId,
                      viewModel = viewModel,
                      onBack = { viewModel.navigateBack() }
                    )
                  }
                }
              }
            }
          }

          // Global License Activation Dialog
          if (showLicenseDialog && userProfile != null) {
            LicenseActivationDialog(
              deviceId = userProfile!!.deviceId,
              isActivated = userProfile!!.isActivated,
              onDismiss = { viewModel.dismissLicenseDialog() },
              onActivate = { key ->
                viewModel.activateLicense(key) {}
              }
            )
          }
        }
      }
    }
  }
}
