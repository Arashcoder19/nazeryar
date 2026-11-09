package com.example.data.repository

import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.model.FinancialStage
import com.example.data.model.Project
import com.example.data.model.ProjectMember
import com.example.data.model.ProjectNote
import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import com.example.data.model.ReportStage
import com.example.data.model.UserProfile
import com.example.data.repository.impl.FinancialRepositoryImpl
import com.example.data.repository.impl.ProjectRepositoryImpl
import com.example.data.repository.impl.ReportRepositoryImpl
import com.example.data.repository.impl.UserProfileRepositoryImpl
import com.example.domain.repository.IFinancialRepository
import com.example.domain.repository.IProjectRepository
import com.example.domain.repository.IReportRepository
import com.example.domain.repository.IUserProfileRepository
import com.example.domain.usecase.ActivateLicenseUseCase
import com.example.domain.usecase.CreateProjectUseCase
import com.example.domain.usecase.ExportProjectArchiveUseCase
import com.example.domain.usecase.GenerateReportPdfUseCase
import com.example.domain.usecase.SubmitReportUseCase
import com.example.util.LicenseManager
import kotlinx.coroutines.flow.Flow
import java.io.File

class NazerYarRepository(
  private val context: Context,
  database: AppDatabase
) {
  val userProfileRepo: IUserProfileRepository = UserProfileRepositoryImpl(database.userProfileDao())
  val projectRepo: IProjectRepository = ProjectRepositoryImpl(
    database.projectDao(),
    database.projectMemberDao(),
    database.projectNoteDao()
  )
  val reportRepo: IReportRepository = ReportRepositoryImpl(database.reportDao())
  val financialRepo: IFinancialRepository = FinancialRepositoryImpl(database.financialDao())

  // Domain UseCases
  val createProjectUseCase = CreateProjectUseCase(context, projectRepo, userProfileRepo)
  val submitReportUseCase = SubmitReportUseCase(projectRepo, reportRepo)
  val generateReportPdfUseCase = GenerateReportPdfUseCase(context, userProfileRepo, projectRepo, reportRepo)
  val exportArchiveUseCase = ExportProjectArchiveUseCase(context, projectRepo, reportRepo, financialRepo)
  val activateLicenseUseCase = ActivateLicenseUseCase(userProfileRepo)

  // User Profile
  val userProfileFlow: Flow<UserProfile?> = userProfileRepo.userProfileFlow
  suspend fun getUserProfile(): UserProfile? = userProfileRepo.getUserProfile()
  suspend fun saveUserProfile(profile: UserProfile) = userProfileRepo.saveUserProfile(profile)
  suspend fun activateLicense(key: String): Boolean = activateLicenseUseCase(key)

  suspend fun isTrialLimitReached(): Boolean {
    val profile = userProfileRepo.getUserProfile()
    if (profile?.isActivated == true) return false
    val activeCount = projectRepo.getActiveProjectCount()
    return activeCount >= LicenseManager.TRIAL_MAX_ACTIVE_PROJECTS
  }

  // Projects
  val activeProjectsFlow: Flow<List<Project>> = projectRepo.activeProjectsFlow
  val archivedProjectsFlow: Flow<List<Project>> = projectRepo.archivedProjectsFlow

  fun getProjectFlow(id: String): Flow<Project?> = projectRepo.getProjectFlow(id)
  suspend fun getProject(id: String): Project? = projectRepo.getProject(id)
  suspend fun createProject(project: Project, members: List<ProjectMember>): Result<Unit> =
    createProjectUseCase(project, members)

  suspend fun updateProject(project: Project) = projectRepo.updateProject(project)
  suspend fun deleteProject(projectId: String) = projectRepo.deleteProject(projectId)

  // Stages & Reports
  fun getStagesForProjectFlow(projectId: String): Flow<List<ReportStage>> =
    projectRepo.getStagesForProjectFlow(projectId)

  suspend fun getStagesForProject(projectId: String): List<ReportStage> =
    projectRepo.getStagesForProject(projectId)

  fun getReportsForProjectFlow(projectId: String): Flow<List<Report>> =
    reportRepo.getReportsForProjectFlow(projectId)

  suspend fun getReportsForProject(projectId: String): List<Report> =
    reportRepo.getReportsForProject(projectId)

  fun getReportsForStageFlow(stageId: String): Flow<List<Report>> =
    reportRepo.getReportsForStageFlow(stageId)

  fun getChecklistForReportFlow(reportId: String): Flow<List<ReportChecklistItem>> =
    reportRepo.getChecklistForReportFlow(reportId)

  suspend fun getChecklistForReport(reportId: String): List<ReportChecklistItem> =
    reportRepo.getChecklistForReport(reportId)

  fun getPhotosForReportFlow(reportId: String): Flow<List<ReportPhoto>> =
    reportRepo.getPhotosForReportFlow(reportId)

  suspend fun getPhotosForReport(reportId: String): List<ReportPhoto> =
    reportRepo.getPhotosForReport(reportId)

  suspend fun createReport(
    report: Report,
    checklistItems: List<ReportChecklistItem>,
    photos: List<ReportPhoto>,
    markStageCompleted: Boolean
  ): Result<Report> = submitReportUseCase(report, checklistItems, photos, markStageCompleted)

  suspend fun deleteReport(reportId: String) = reportRepo.deleteReport(reportId)

  suspend fun generatePdf(project: Project, report: Report, includeAttachment: Boolean): Result<File> =
    generateReportPdfUseCase(project, report, includeAttachment)

  suspend fun exportArchive(project: Project): Result<File> =
    exportArchiveUseCase(project)

  // Financials
  fun getFinancialStagesFlow(projectId: String): Flow<List<FinancialStage>> =
    financialRepo.getFinancialStagesFlow(projectId)

  suspend fun getFinancialStages(projectId: String): List<FinancialStage> =
    financialRepo.getFinancialStages(projectId)

  suspend fun insertFinancialStage(stage: FinancialStage) = financialRepo.insertFinancialStage(stage)
  suspend fun updateFinancialStage(stage: FinancialStage) = financialRepo.updateFinancialStage(stage)
  suspend fun deleteFinancialStage(stageId: String) = financialRepo.deleteFinancialStage(stageId)

  // Members
  fun getMembersFlow(projectId: String): Flow<List<ProjectMember>> =
    projectRepo.getMembersFlow(projectId)

  suspend fun getMembers(projectId: String): List<ProjectMember> =
    projectRepo.getMembers(projectId)

  suspend fun insertMember(member: ProjectMember) = projectRepo.insertMember(member)
  suspend fun deleteMember(memberId: String) = projectRepo.deleteMember(memberId)

  // Notes
  fun getNotesFlow(projectId: String): Flow<List<ProjectNote>> =
    projectRepo.getNotesFlow(projectId)

  suspend fun getNotes(projectId: String): List<ProjectNote> =
    projectRepo.getNotes(projectId)

  suspend fun insertNote(note: ProjectNote) = projectRepo.insertNote(note)
  suspend fun deleteNote(noteId: String) = projectRepo.deleteNote(noteId)
}
