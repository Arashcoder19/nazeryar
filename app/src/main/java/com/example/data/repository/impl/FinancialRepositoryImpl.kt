package com.example.data.repository.impl

import com.example.data.dao.FinancialDao
import com.example.data.model.FinancialStage
import com.example.domain.repository.IFinancialRepository
import kotlinx.coroutines.flow.Flow

class FinancialRepositoryImpl(
  private val financialDao: FinancialDao
) : IFinancialRepository {

  override fun getFinancialStagesFlow(projectId: String): Flow<List<FinancialStage>> =
    financialDao.getFinancialStagesFlow(projectId)

  override suspend fun getFinancialStages(projectId: String): List<FinancialStage> =
    financialDao.getFinancialStages(projectId)

  override suspend fun insertFinancialStage(stage: FinancialStage) =
    financialDao.insertFinancialStage(stage)

  override suspend fun updateFinancialStage(stage: FinancialStage) =
    financialDao.updateFinancialStage(stage)

  override suspend fun deleteFinancialStage(stageId: String) =
    financialDao.deleteFinancialStageById(stageId)
}
