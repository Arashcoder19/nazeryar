package com.example.data.repository.impl

import com.example.data.dao.ReportDao
import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import com.example.domain.repository.IReportRepository
import kotlinx.coroutines.flow.Flow

class ReportRepositoryImpl(
  private val reportDao: ReportDao
) : IReportRepository {

  override fun getReportsForProjectFlow(projectId: String): Flow<List<Report>> =
    reportDao.getReportsForProjectFlow(projectId)

  override suspend fun getReportsForProject(projectId: String): List<Report> =
    reportDao.getReportsForProject(projectId)

  override fun getReportsForStageFlow(stageId: String): Flow<List<Report>> =
    reportDao.getReportsForStageFlow(stageId)

  override suspend fun getReportById(reportId: String): Report? =
    reportDao.getReportById(reportId)

  override suspend fun insertReport(report: Report) = reportDao.insertReport(report)
  override suspend fun updateReport(report: Report) = reportDao.updateReport(report)
  override suspend fun deleteReport(reportId: String) = reportDao.deleteReportById(reportId)

  override fun getChecklistForReportFlow(reportId: String): Flow<List<ReportChecklistItem>> =
    reportDao.getChecklistForReportFlow(reportId)

  override suspend fun getChecklistForReport(reportId: String): List<ReportChecklistItem> =
    reportDao.getChecklistForReport(reportId)

  override suspend fun insertChecklistItems(items: List<ReportChecklistItem>) =
    reportDao.insertChecklistItems(items)

  override fun getPhotosForReportFlow(reportId: String): Flow<List<ReportPhoto>> =
    reportDao.getPhotosForReportFlow(reportId)

  override suspend fun getPhotosForReport(reportId: String): List<ReportPhoto> =
    reportDao.getPhotosForReport(reportId)

  override suspend fun insertPhotos(photos: List<ReportPhoto>) =
    reportDao.insertPhotos(photos)

  override suspend fun deletePhoto(photoId: String) =
    reportDao.deletePhotoById(photoId)
}
