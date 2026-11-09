package com.example.domain.usecase

import android.content.Context
import com.example.data.model.Project
import com.example.data.model.ProjectMember
import com.example.data.model.ReportStage
import com.example.domain.repository.IProjectRepository
import com.example.domain.repository.IUserProfileRepository
import com.example.util.ConfigManager
import com.example.util.LicenseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class CreateProjectUseCase(
  private val context: Context,
  private val projectRepository: IProjectRepository,
  private val userProfileRepository: IUserProfileRepository
) {
  suspend operator fun invoke(project: Project, members: List<ProjectMember>): Result<Unit> = withContext(Dispatchers.IO) {
    val profile = userProfileRepository.getUserProfile()
    val isActivated = profile?.isActivated == true
    val activeCount = projectRepository.getActiveProjectCount()

    if (!isActivated && activeCount >= LicenseManager.TRIAL_MAX_ACTIVE_PROJECTS) {
      return@withContext Result.failure(IllegalStateException("TRIAL_LIMIT_REACHED"))
    }

    projectRepository.insertProject(project)

    // Generate ReportStages from JSON config template
    val configStages = ConfigManager.getStagesForGroup(context, project.buildingGroup)
    val stages = configStages.map { cs ->
      ReportStage(
        id = UUID.randomUUID().toString(),
        projectId = project.id,
        stageNumber = cs.stageNumber,
        stageTitle = cs.title,
        isCompleted = false,
        defaultChecklistTemplateId = "${project.buildingGroup.code}_${cs.stageNumber}"
      )
    }
    projectRepository.insertStages(stages)

    if (members.isNotEmpty()) {
      projectRepository.insertMembers(members)
    }

    Result.success(Unit)
  }
}
