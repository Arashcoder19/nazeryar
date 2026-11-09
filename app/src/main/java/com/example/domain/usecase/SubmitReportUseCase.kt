package com.example.domain.usecase

import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import com.example.domain.repository.IProjectRepository
import com.example.domain.repository.IReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubmitReportUseCase(
  private val projectRepository: IProjectRepository,
  private val reportRepository: IReportRepository
) {
  suspend operator fun invoke(
    report: Report,
    checklistItems: List<ReportChecklistItem>,
    photos: List<ReportPhoto>,
    markStageCompleted: Boolean
  ): Result<Report> = withContext(Dispatchers.IO) {
    reportRepository.insertReport(report)

    if (checklistItems.isNotEmpty()) {
      reportRepository.insertChecklistItems(checklistItems)
    }

    if (photos.isNotEmpty()) {
      reportRepository.insertPhotos(photos)
    }

    if (markStageCompleted) {
      val stage = projectRepository.getStageById(report.stageId)
      if (stage != null) {
        projectRepository.updateStage(stage.copy(isCompleted = true))
      }
    }

    // Touch project updatedAt
    val project = projectRepository.getProject(report.projectId)
    if (project != null) {
      projectRepository.updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    Result.success(report)
  }
}
