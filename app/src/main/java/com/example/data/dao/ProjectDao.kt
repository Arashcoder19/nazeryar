package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Project
import com.example.data.model.ReportStage
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
  @Query("SELECT * FROM projects WHERE lifecycleStatus != 'COMPLETED' ORDER BY updatedAt DESC")
  fun getActiveProjectsFlow(): Flow<List<Project>>

  @Query("SELECT * FROM projects WHERE lifecycleStatus = 'COMPLETED' ORDER BY completedDate DESC, updatedAt DESC")
  fun getArchivedProjectsFlow(): Flow<List<Project>>

  @Query("SELECT * FROM projects WHERE id = :projectId")
  fun getProjectByIdFlow(projectId: String): Flow<Project?>

  @Query("SELECT * FROM projects WHERE id = :projectId")
  suspend fun getProjectById(projectId: String): Project?

  @Query("SELECT COUNT(*) FROM projects WHERE lifecycleStatus != 'COMPLETED'")
  suspend fun getActiveProjectCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: Project)

  @Update
  suspend fun updateProject(project: Project)

  @Query("DELETE FROM projects WHERE id = :projectId")
  suspend fun deleteProjectById(projectId: String)

  // Report Stages
  @Query("SELECT * FROM report_stages WHERE projectId = :projectId ORDER BY stageNumber ASC")
  fun getStagesForProjectFlow(projectId: String): Flow<List<ReportStage>>

  @Query("SELECT * FROM report_stages WHERE projectId = :projectId ORDER BY stageNumber ASC")
  suspend fun getStagesForProject(projectId: String): List<ReportStage>

  @Query("SELECT * FROM report_stages WHERE id = :stageId")
  suspend fun getStageById(stageId: String): ReportStage?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStages(stages: List<ReportStage>)

  @Update
  suspend fun updateStage(stage: ReportStage)
}
