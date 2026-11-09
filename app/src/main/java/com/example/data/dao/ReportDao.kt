package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
  @Query("SELECT * FROM reports WHERE projectId = :projectId ORDER BY reportDate DESC")
  fun getReportsForProjectFlow(projectId: String): Flow<List<Report>>

  @Query("SELECT * FROM reports WHERE projectId = :projectId ORDER BY reportDate DESC")
  suspend fun getReportsForProject(projectId: String): List<Report>

  @Query("SELECT * FROM reports WHERE stageId = :stageId ORDER BY reportDate DESC")
  fun getReportsForStageFlow(stageId: String): Flow<List<Report>>

  @Query("SELECT * FROM reports WHERE id = :reportId")
  suspend fun getReportById(reportId: String): Report?

  @Query("SELECT * FROM reports WHERE projectId = :projectId ORDER BY reportDate DESC LIMIT 1")
  suspend fun getLatestReportForProject(projectId: String): Report?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReport(report: Report)

  @Update
  suspend fun updateReport(report: Report)

  @Query("DELETE FROM reports WHERE id = :reportId")
  suspend fun deleteReportById(reportId: String)

  // Checklist Items
  @Query("SELECT * FROM report_checklist_items WHERE reportId = :reportId ORDER BY sortOrder ASC")
  fun getChecklistForReportFlow(reportId: String): Flow<List<ReportChecklistItem>>

  @Query("SELECT * FROM report_checklist_items WHERE reportId = :reportId ORDER BY sortOrder ASC")
  suspend fun getChecklistForReport(reportId: String): List<ReportChecklistItem>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChecklistItems(items: List<ReportChecklistItem>)

  @Update
  suspend fun updateChecklistItem(item: ReportChecklistItem)

  // Photos
  @Query("SELECT * FROM report_photos WHERE reportId = :reportId ORDER BY sortOrder ASC")
  fun getPhotosForReportFlow(reportId: String): Flow<List<ReportPhoto>>

  @Query("SELECT * FROM report_photos WHERE reportId = :reportId ORDER BY sortOrder ASC")
  suspend fun getPhotosForReport(reportId: String): List<ReportPhoto>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPhotos(photos: List<ReportPhoto>)

  @Query("DELETE FROM report_photos WHERE id = :photoId")
  suspend fun deletePhotoById(photoId: String)
}
