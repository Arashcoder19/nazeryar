package com.example.domain.usecase

import android.content.Context
import com.example.data.model.Project
import com.example.data.model.Report
import com.example.domain.repository.IProjectRepository
import com.example.domain.repository.IReportRepository
import com.example.domain.repository.IUserProfileRepository
import com.example.util.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GenerateReportPdfUseCase(
  private val context: Context,
  private val userProfileRepository: IUserProfileRepository,
  private val projectRepository: IProjectRepository,
  private val reportRepository: IReportRepository
) {
  suspend operator fun invoke(
    project: Project,
    report: Report,
    includeAttachment: Boolean
  ): Result<File> = withContext(Dispatchers.IO) {
    val profile = userProfileRepository.getUserProfile()
      ?: return@withContext Result.failure(IllegalStateException("PROFILE_NOT_FOUND"))

    val stages = projectRepository.getStagesForProject(project.id)
    val stageTitle = stages.find { it.id == report.stageId }?.stageTitle ?: "مرحله ${report.stageNumber}"
    val checklist = reportRepository.getChecklistForReport(report.id)
    val photos = reportRepository.getPhotosForReport(report.id)

    runCatching {
      val file = PdfGenerator.generateReportPdf(
        context = context,
        userProfile = profile,
        project = project,
        report = report,
        stageTitle = stageTitle,
        checklistItems = checklist,
        photos = photos,
        includeAttachmentPage = includeAttachment
      )
      // Save pdfFilePath into report record
      reportRepository.updateReport(report.copy(pdfFilePath = file.absolutePath))
      file
    }
  }
}
