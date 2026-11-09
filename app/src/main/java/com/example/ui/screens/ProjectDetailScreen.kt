package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.FileProvider
import com.example.data.model.FinancialStage
import com.example.data.model.ProjectLifecycleStatus
import com.example.data.model.ProjectMember
import com.example.data.model.ProjectNote
import com.example.data.model.Report
import com.example.ui.screens.detail.AddFinancialStageDialog
import com.example.ui.screens.detail.AddMemberDialog
import com.example.ui.screens.detail.AddNoteDialog
import com.example.ui.screens.detail.CompleteProjectDialog
import com.example.ui.screens.detail.DeleteProjectDialog
import com.example.ui.screens.detail.ExportReportPdfDialog
import com.example.ui.screens.detail.FinancialTabContent
import com.example.ui.screens.detail.MembersTabContent
import com.example.ui.screens.detail.NotesTabContent
import com.example.ui.screens.detail.ProjectDetailHeader
import com.example.ui.screens.detail.ReportsTabContent
import com.example.ui.theme.ConstructionAmber
import com.example.ui.theme.SafetyRed
import com.example.ui.viewmodel.NazerYarViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
  projectId: String,
  viewModel: NazerYarViewModel,
  onBack: () -> Unit,
  onNewReport: (stageId: String?) -> Unit,
  onViewReport: (reportId: String) -> Unit
) {
  val context = LocalContext.current
  val projectFlow = remember(projectId) { viewModel.getProjectFlow(projectId) }
  val project by projectFlow.collectAsState(initial = null)

  val stagesFlow = remember(projectId) { viewModel.getStagesFlow(projectId) }
  val stages by stagesFlow.collectAsState(initial = emptyList())

  val reportsFlow = remember(projectId) { viewModel.getReportsFlow(projectId) }
  val reports by reportsFlow.collectAsState(initial = emptyList())

  val financialsFlow = remember(projectId) { viewModel.getFinancialStagesFlow(projectId) }
  val financials by financialsFlow.collectAsState(initial = emptyList())

  val membersFlow = remember(projectId) { viewModel.getMembersFlow(projectId) }
  val members by membersFlow.collectAsState(initial = emptyList())

  val notesFlow = remember(projectId) { viewModel.getNotesFlow(projectId) }
  val notes by notesFlow.collectAsState(initial = emptyList())

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var menuExpanded by remember { mutableStateOf(false) }

  // Dialog States
  var showCompleteDialog by remember { mutableStateOf(false) }
  var showDeleteDialog by remember { mutableStateOf(false) }
  var showAddFinancialDialog by remember { mutableStateOf(false) }
  var isFinancialExtraArea by remember { mutableStateOf(false) }
  var showAddMemberDialog by remember { mutableStateOf(false) }
  var showAddNoteDialog by remember { mutableStateOf(false) }
  var reportToExportPdf by remember { mutableStateOf<Report?>(null) }

  BackHandler { onBack() }

  if (project == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("در حال بارگذاری پروژه...")
    }
    return
  }

  val currentProject = project!!

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = currentProject.title, maxLines = 1, fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
          }
        },
        actions = {
          IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "عملیات")
          }
          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
          ) {
            if (currentProject.lifecycleStatus != ProjectLifecycleStatus.CONTRACT_SIGNED) {
              DropdownMenuItem(
                text = { Text("ثبت امضای قرارداد") },
                onClick = {
                  menuExpanded = false
                  viewModel.setProjectStatus(currentProject, ProjectLifecycleStatus.CONTRACT_SIGNED)
                }
              )
            }
            if (currentProject.lifecycleStatus != ProjectLifecycleStatus.SUPERVISION_COMMITMENT_SIGNED) {
              DropdownMenuItem(
                text = { Text("ثبت تعهد نظارت") },
                onClick = {
                  menuExpanded = false
                  viewModel.setProjectStatus(currentProject, ProjectLifecycleStatus.SUPERVISION_COMMITMENT_SIGNED)
                }
              )
            }
            if (currentProject.lifecycleStatus == ProjectLifecycleStatus.TEMPORARILY_INACTIVE) {
              DropdownMenuItem(
                text = { Text("فعال‌سازی مجدد کارگاه") },
                onClick = {
                  menuExpanded = false
                  viewModel.setProjectStatus(currentProject, ProjectLifecycleStatus.ACTIVE)
                }
              )
            } else if (currentProject.lifecycleStatus != ProjectLifecycleStatus.COMPLETED) {
              DropdownMenuItem(
                text = { Text("تغییر به غیرفعال موقت") },
                onClick = {
                  menuExpanded = false
                  viewModel.setProjectStatus(currentProject, ProjectLifecycleStatus.TEMPORARILY_INACTIVE)
                }
              )
            }
            if (currentProject.lifecycleStatus != ProjectLifecycleStatus.COMPLETED) {
              DropdownMenuItem(
                text = { Text("اتمام قرارداد / انتقال به آرشیو", color = ConstructionAmber) },
                onClick = {
                  menuExpanded = false
                  showCompleteDialog = true
                }
              )
            }
            DropdownMenuItem(
              text = { Text("حذف پروژه", color = SafetyRed) },
              onClick = {
                menuExpanded = false
                showDeleteDialog = true
              }
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primary,
          titleContentColor = Color.White,
          navigationIconContentColor = Color.White,
          actionIconContentColor = Color.White
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      ProjectDetailHeader(project = currentProject)

      TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        Tab(
          selected = selectedTabIndex == 0,
          onClick = { selectedTabIndex = 0 },
          text = { Text("گزارش‌ها (${reports.size})", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTabIndex == 1,
          onClick = { selectedTabIndex = 1 },
          text = { Text("مالی (${financials.size})", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTabIndex == 2,
          onClick = { selectedTabIndex = 2 },
          text = { Text("اعضا (${members.size})", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTabIndex == 3,
          onClick = { selectedTabIndex = 3 },
          text = { Text("یادداشت‌ها (${notes.size})", fontWeight = FontWeight.Bold) }
        )
      }

      when (selectedTabIndex) {
        0 -> ReportsTabContent(
          stages = stages,
          reports = reports,
          onNewReport = onNewReport,
          onViewReport = onViewReport,
          onExportPdf = { reportToExportPdf = it }
        )
        1 -> FinancialTabContent(
          financials = financials,
          onAddStandardStage = {
            isFinancialExtraArea = false
            showAddFinancialDialog = true
          },
          onAddExtraAreaStage = {
            isFinancialExtraArea = true
            showAddFinancialDialog = true
          },
          onToggleReceived = { stage, checked ->
            viewModel.updateFinancialStage(
              stage.copy(
                isReceived = checked,
                receivedDate = if (checked) System.currentTimeMillis() else null
              )
            )
          },
          onDeleteStage = { viewModel.deleteFinancialStage(it) }
        )
        2 -> MembersTabContent(
          members = members,
          onAddMember = { showAddMemberDialog = true },
          onDeleteMember = { viewModel.deleteMember(it) }
        )
        3 -> NotesTabContent(
          notes = notes,
          onAddNote = { showAddNoteDialog = true },
          onDeleteNote = { viewModel.deleteNote(it) }
        )
      }
    }
  }

  // Dialogs
  if (showCompleteDialog) {
    CompleteProjectDialog(
      onDismiss = { showCompleteDialog = false },
      onConfirm = {
        showCompleteDialog = false
        viewModel.setProjectStatus(currentProject, ProjectLifecycleStatus.COMPLETED)
      }
    )
  }

  if (showDeleteDialog) {
    DeleteProjectDialog(
      onDismiss = { showDeleteDialog = false },
      onConfirm = {
        showDeleteDialog = false
        viewModel.deleteProject(currentProject.id)
      }
    )
  }

  if (showAddFinancialDialog) {
    AddFinancialStageDialog(
      isExtraArea = isFinancialExtraArea,
      onDismiss = { showAddFinancialDialog = false },
      onConfirm = { title, amount, note ->
        viewModel.addFinancialStage(
          FinancialStage(
            id = UUID.randomUUID().toString(),
            projectId = currentProject.id,
            title = title,
            sortOrder = financials.size,
            isExtraArea = isFinancialExtraArea,
            amount = amount,
            note = note
          )
        )
        showAddFinancialDialog = false
      }
    )
  }

  if (showAddMemberDialog) {
    AddMemberDialog(
      onDismiss = { showAddMemberDialog = false },
      onConfirm = { name, role, phone, isSup, isCoord ->
        viewModel.addMember(
          ProjectMember(
            id = UUID.randomUUID().toString(),
            projectId = currentProject.id,
            fullName = name,
            role = role,
            phoneNumber = phone,
            isSupervisor = isSup,
            isCoordinatingSupervisor = isCoord,
            isAppUser = false,
            sortOrder = members.size
          )
        )
        showAddMemberDialog = false
      }
    )
  }

  if (showAddNoteDialog) {
    AddNoteDialog(
      onDismiss = { showAddNoteDialog = false },
      onConfirm = { content ->
        viewModel.addNote(
          ProjectNote(
            id = UUID.randomUUID().toString(),
            projectId = currentProject.id,
            content = content
          )
        )
        showAddNoteDialog = false
      }
    )
  }

  if (reportToExportPdf != null) {
    ExportReportPdfDialog(
      report = reportToExportPdf!!,
      onDismiss = { reportToExportPdf = null },
      onConfirm = { includeAttachment ->
        val selectedReport = reportToExportPdf!!
        reportToExportPdf = null
        viewModel.generatePdf(currentProject, selectedReport, includeAttachment) { file ->
          if (file != null) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
              setDataAndType(uri, "application/pdf")
              addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            runCatching {
              context.startActivity(Intent.createChooser(intent, "مشاهده / ارسال فایل PDF گزارش"))
            }
          }
        }
      }
    )
  }
}
