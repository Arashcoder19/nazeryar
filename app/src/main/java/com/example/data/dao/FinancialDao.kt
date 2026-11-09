package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FinancialStage
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialDao {
  @Query("SELECT * FROM financial_stages WHERE projectId = :projectId ORDER BY sortOrder ASC, createdAt ASC")
  fun getFinancialStagesFlow(projectId: String): Flow<List<FinancialStage>>

  @Query("SELECT * FROM financial_stages WHERE projectId = :projectId ORDER BY sortOrder ASC, createdAt ASC")
  suspend fun getFinancialStages(projectId: String): List<FinancialStage>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFinancialStage(stage: FinancialStage)

  @Update
  suspend fun updateFinancialStage(stage: FinancialStage)

  @Query("DELETE FROM financial_stages WHERE id = :stageId")
  suspend fun deleteFinancialStageById(stageId: String)
}
