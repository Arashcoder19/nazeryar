package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.BuildingGroup
import com.example.data.model.EngineerDiscipline
import com.example.data.model.EngineerGrade
import com.example.data.model.FinancialStage
import com.example.data.model.Project
import com.example.data.model.ProjectLifecycleStatus
import com.example.data.model.ProjectMember
import com.example.data.model.ProjectNote
import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import com.example.data.model.ReportStage
import com.example.data.model.UserProfile
import com.example.data.repository.NazerYarRepository
import com.example.util.DateUtils
import com.example.util.ExcelExportManager
import com.example.util.LicenseManager
import com.example.util.PdfGenerator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

sealed class Screen {
  data object ProjectsList : Screen()
  data object ArchiveList : Screen()
  data object Profile : Screen()
  data object Settings : Screen()
  data class ProjectDetail(val projectId: String) : Screen()
  data object NewProjectWizard : Screen()
  data class NewReportWizard(val projectId: String, val initialStageId: String? = null) : Screen()
  data class ViewReportDetail(val projectId: String, val reportId: String) : Screen()
}

class NazerYarViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = NazerYarRepository(
    application.applicationContext,
    AppDatabase.getDatabase(application.applicationContext)
  )

  val userProfile: StateFlow<UserProfile?> = repository.userProfileFlow
    .stateIn(viewModelScope, SharingStarted.Eagerly, null)

  val activeProjects: StateFlow<List<Project>> = repository.activeProjectsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val archivedProjects: StateFlow<List<Project>> = repository.archivedProjectsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _currentScreen = MutableStateFlow<Screen>(Screen.ProjectsList)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  private val screenBackStack = mutableListOf<Screen>()

  private val _toastEvent = MutableSharedFlow<String>()
  val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

  private val _showLicenseDialog = MutableStateFlow(false)
  val showLicenseDialog: StateFlow<Boolean> = _showLicenseDialog.asStateFlow()

  private val _maxPhotosSetting = MutableStateFlow(2)
  val maxPhotosSetting: StateFlow<Int> = _maxPhotosSetting.asStateFlow()

  private val _pdfGeneratedFile = MutableStateFlow<File?>(null)
  val pdfGeneratedFile: StateFlow<File?> = _pdfGeneratedFile.asStateFlow()

  fun navigateTo(screen: Screen) {
    screenBackStack.add(_currentScreen.value)
    _currentScreen.value = screen
  }

  fun navigateBack(): Boolean {
    if (screenBackStack.isNotEmpty()) {
      _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
      return true
    }
    return false
  }

  fun setMaxPhotos(limit: Int) {
    _maxPhotosSetting.value = limit.coerceIn(1, 6)
  }

  fun openLicenseDialog() {
    _showLicenseDialog.value = true
  }

  fun dismissLicenseDialog() {
    _showLicenseDialog.value = false
  }

  fun clearPdfFile() {
    _pdfGeneratedFile.value = null
  }

  // Profile Management
  fun saveProfile(
    firstName: String,
    lastName: String,
    membershipNumber: String,
    licenseNumber: String,
    grade: EngineerGrade,
    discipline: EngineerDiscipline
  ) {
    viewModelScope.launch {
      val existing = userProfile.value
      val deviceId = existing?.deviceId ?: LicenseManager.getDeviceId(getApplication())
      val profile = UserProfile(
        id = existing?.id ?: "primary_user_profile",
        firstName = firstName.trim(),
        lastName = lastName.trim(),
        membershipNumber = membershipNumber.trim(),
        licenseNumber = licenseNumber.trim(),
        grade = grade,
        discipline = discipline,
        isActivated = existing?.isActivated ?: false,
        licenseKey = existing?.licenseKey,
        deviceId = deviceId
      )
      repository.saveUserProfile(profile)
      _toastEvent.emit("مشخصات مهندس ناظر با موفقیت ذخیره شد")
    }
  }

  // License Activation
  fun activateLicense(key: String, onComplete: (Boolean) -> Unit) {
    viewModelScope.launch {
      val success = repository.activateLicense(key)
      if (success) {
        _toastEvent.emit("لایسنس با موفقیت فعال شد! محدودیت‌ها برداشته شدند.")
        _showLicenseDialog.value = false
      } else {
        _toastEvent.emit("کد فعال‌سازی نامعتبر است. لطفاً کد را بررسی کنید.")
      }
      onComplete(success)
    }
  }

  // Project Creation
  fun attemptStartNewProject(onAllowed: () -> Unit) {
    viewModelScope.launch {
      val isTrialFull = repository.isTrialLimitReached()
      if (isTrialFull) {
        _toastEvent.emit("محدودیت نسخه آزمایشی (۱ پروژه). لطفاً نسخه کامل را فعال کنید.")
        _showLicenseDialog.value = true
      } else {
        onAllowed()
      }
    }
  }

  fun createProject(
    project: Project,
    members: List<ProjectMember>,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.createProject(project, members)
      if (result.isSuccess) {
        _toastEvent.emit("پروژه جدید با موفقیت ایجاد شد")
        onSuccess()
      } else {
        _toastEvent.emit("خطا در ایجاد پروژه: نسخه آزمایشی محدود به ۱ پروژه است.")
        _showLicenseDialog.value = true
      }
    }
  }

  fun updateProject(project: Project) {
    viewModelScope.launch {
      repository.updateProject(project)
      _toastEvent.emit("اطلاعات پروژه به‌روزرسانی شد")
    }
  }

  fun deleteProject(projectId: String) {
    viewModelScope.launch {
      repository.deleteProject(projectId)
      _toastEvent.emit("پروژه با موفقیت حذف شد")
      navigateTo(Screen.ProjectsList)
    }
  }

  fun setProjectStatus(project: Project, newStatus: ProjectLifecycleStatus) {
    viewModelScope.launch {
      val updated = project.copy(
        lifecycleStatus = newStatus,
        contractSignedDate = if (newStatus == ProjectLifecycleStatus.CONTRACT_SIGNED && project.contractSignedDate == null) System.currentTimeMillis() else project.contractSignedDate,
        supervisionCommitmentDate = if (newStatus == ProjectLifecycleStatus.SUPERVISION_COMMITMENT_SIGNED && project.supervisionCommitmentDate == null) System.currentTimeMillis() else project.supervisionCommitmentDate,
        completedDate = if (newStatus == ProjectLifecycleStatus.COMPLETED) System.currentTimeMillis() else project.completedDate,
        updatedAt = System.currentTimeMillis()
      )
      repository.updateProject(updated)
      if (newStatus == ProjectLifecycleStatus.COMPLETED) {
        _toastEvent.emit("پروژه به وضعیت پایان‌یافته تغییر یافت و به بخش آرشیو منتقل شد.")
        navigateTo(Screen.ArchiveList)
      } else {
        _toastEvent.emit("وضعیت پروژه به «${newStatus.titleFa}» تغییر یافت.")
      }
    }
  }

  // Reports
  fun createReport(
    report: Report,
    checklistItems: List<ReportChecklistItem>,
    photos: List<ReportPhoto>,
    markStageCompleted: Boolean,
    onSuccess: (Report) -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.createReport(report, checklistItems, photos, markStageCompleted)
      if (result.isSuccess) {
        _toastEvent.emit("گزارش مرحله با موفقیت ثبت شد")
        onSuccess(result.getOrThrow())
      }
    }
  }

  fun deleteReport(reportId: String) {
    viewModelScope.launch {
      repository.deleteReport(reportId)
      _toastEvent.emit("گزارش با موفقیت حذف شد")
    }
  }

  fun generatePdf(
    project: Project,
    report: Report,
    includeAttachment: Boolean,
    onComplete: (File?) -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.generatePdf(project, report, includeAttachment)
      if (result.isSuccess) {
        val file = result.getOrThrow()
        _pdfGeneratedFile.value = file
        _toastEvent.emit("فایل PDF رسمی با موفقیت تولید شد")
        onComplete(file)
      } else {
        _toastEvent.emit("خطا در تولید PDF: ${result.exceptionOrNull()?.localizedMessage ?: "نامشخص"}")
        onComplete(null)
      }
    }
  }

  // Export Completed Project Archive
  fun exportCompletedArchive(project: Project, onComplete: (File?) -> Unit) {
    viewModelScope.launch {
      val result = repository.exportArchive(project)
      if (result.isSuccess) {
        val zipFile = result.getOrThrow()
        _toastEvent.emit("آرشیو داده‌های پروژه در فایل فشرده ذخیره شد")
        onComplete(zipFile)
      } else {
        _toastEvent.emit("خطا در صدور آرشیو: ${result.exceptionOrNull()?.localizedMessage ?: "نامشخص"}")
        onComplete(null)
      }
    }
  }

  // Financials
  fun addFinancialStage(stage: FinancialStage) {
    viewModelScope.launch {
      repository.insertFinancialStage(stage)
      _toastEvent.emit("مرحله مالی اضافه شد")
    }
  }

  fun updateFinancialStage(stage: FinancialStage) {
    viewModelScope.launch {
      repository.updateFinancialStage(stage)
      _toastEvent.emit("مرحله مالی به‌روزرسانی شد")
    }
  }

  fun deleteFinancialStage(stageId: String) {
    viewModelScope.launch {
      repository.deleteFinancialStage(stageId)
      _toastEvent.emit("مرحله مالی حذف شد")
    }
  }

  // Members
  fun addMember(member: ProjectMember) {
    viewModelScope.launch {
      repository.insertMember(member)
      _toastEvent.emit("عضو جدید به پروژه افزوده شد")
    }
  }

  fun deleteMember(memberId: String) {
    viewModelScope.launch {
      repository.deleteMember(memberId)
      _toastEvent.emit("عضو پروژه حذف شد")
    }
  }

  // Notes
  fun addNote(note: ProjectNote) {
    viewModelScope.launch {
      repository.insertNote(note)
      _toastEvent.emit("یادداشت ذخیره شد")
    }
  }

  fun deleteNote(noteId: String) {
    viewModelScope.launch {
      repository.deleteNote(noteId)
      _toastEvent.emit("یادداشت حذف شد")
    }
  }

  // Flow queries for screens
  fun getProjectFlow(id: String) = repository.getProjectFlow(id)
  fun getStagesFlow(projectId: String) = repository.getStagesForProjectFlow(projectId)
  fun getReportsFlow(projectId: String) = repository.getReportsForProjectFlow(projectId)
  fun getFinancialStagesFlow(projectId: String) = repository.getFinancialStagesFlow(projectId)
  fun getMembersFlow(projectId: String) = repository.getMembersFlow(projectId)
  fun getNotesFlow(projectId: String) = repository.getNotesFlow(projectId)
  fun getChecklistForReportFlow(reportId: String) = repository.getChecklistForReportFlow(reportId)
  fun getPhotosForReportFlow(reportId: String) = repository.getPhotosForReportFlow(reportId)
}
