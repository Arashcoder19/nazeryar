package com.example.domain.usecase

import android.content.Context
import com.example.data.model.Project
import com.example.domain.repository.IFinancialRepository
import com.example.domain.repository.IProjectRepository
import com.example.domain.repository.IReportRepository
import com.example.util.ExcelExportManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ExportProjectArchiveUseCase(
  private val context: Context,
  private val projectRepository: IProjectRepository,
  private val reportRepository: IReportRepository,
  private val financialRepository: IFinancialRepository
) {
  suspend operator fun invoke(project: Project): Result<File> = withContext(Dispatchers.IO) {
    runCatching {
      val stages = projectRepository.getStagesForProject(project.id)
      val reports = reportRepository.getReportsForProject(project.id)
      val financials = financialRepository.getFinancialStages(project.id)
      val members = projectRepository.getMembers(project.id)
      val notes = projectRepository.getNotes(project.id)

      ExcelExportManager.exportProjectArchiveZip(
        context = context,
        project = project,
        stages = stages,
        reports = reports,
        financials = financials,
        members = members,
        notes = notes
      )
    }
  }
}
