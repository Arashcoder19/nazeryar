package com.example.domain.repository

import com.example.data.model.FinancialStage
import com.example.data.model.Project
import com.example.data.model.ProjectMember
import com.example.data.model.ProjectNote
import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import com.example.data.model.ReportStage
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface IUserProfileRepository {
  val userProfileFlow: Flow<UserProfile?>
  suspend fun getUserProfile(): UserProfile?
  suspend fun saveUserProfile(profile: UserProfile)
  suspend fun updateActivation(profileId: String, isActivated: Boolean, licenseKey: String?)
}

interface IProjectRepository {
  val activeProjectsFlow: Flow<List<Project>>
  val archivedProjectsFlow: Flow<List<Project>>
  fun getProjectFlow(id: String): Flow<Project?>
  suspend fun getProject(id: String): Project?
  suspend fun insertProject(project: Project)
  suspend fun updateProject(project: Project)
  suspend fun deleteProject(projectId: String)
  suspend fun getActiveProjectCount(): Int

  fun getStagesForProjectFlow(projectId: String): Flow<List<ReportStage>>
  suspend fun getStagesForProject(projectId: String): List<ReportStage>
  suspend fun insertStages(stages: List<ReportStage>)
  suspend fun updateStage(stage: ReportStage)
  suspend fun getStageById(stageId: String): ReportStage?

  fun getMembersFlow(projectId: String): Flow<List<ProjectMember>>
  suspend fun getMembers(projectId: String): List<ProjectMember>
  suspend fun insertMembers(members: List<ProjectMember>)
  suspend fun insertMember(member: ProjectMember)
  suspend fun deleteMember(memberId: String)
  suspend fun clearCoordinatingSupervisor(projectId: String)

  fun getNotesFlow(projectId: String): Flow<List<ProjectNote>>
  suspend fun getNotes(projectId: String): List<ProjectNote>
  suspend fun insertNote(note: ProjectNote)
  suspend fun deleteNote(noteId: String)
}

interface IReportRepository {
  fun getReportsForProjectFlow(projectId: String): Flow<List<Report>>
  suspend fun getReportsForProject(projectId: String): List<Report>
  fun getReportsForStageFlow(stageId: String): Flow<List<Report>>
  suspend fun getReportById(reportId: String): Report?
  suspend fun insertReport(report: Report)
  suspend fun updateReport(report: Report)
  suspend fun deleteReport(reportId: String)

  fun getChecklistForReportFlow(reportId: String): Flow<List<ReportChecklistItem>>
  suspend fun getChecklistForReport(reportId: String): List<ReportChecklistItem>
  suspend fun insertChecklistItems(items: List<ReportChecklistItem>)

  fun getPhotosForReportFlow(reportId: String): Flow<List<ReportPhoto>>
  suspend fun getPhotosForReport(reportId: String): List<ReportPhoto>
  suspend fun insertPhotos(photos: List<ReportPhoto>)
  suspend fun deletePhoto(photoId: String)
}

interface IFinancialRepository {
  fun getFinancialStagesFlow(projectId: String): Flow<List<FinancialStage>>
  suspend fun getFinancialStages(projectId: String): List<FinancialStage>
  suspend fun insertFinancialStage(stage: FinancialStage)
  suspend fun updateFinancialStage(stage: FinancialStage)
  suspend fun deleteFinancialStage(stageId: String)
}
