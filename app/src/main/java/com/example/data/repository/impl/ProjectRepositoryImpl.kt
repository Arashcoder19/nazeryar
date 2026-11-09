package com.example.data.repository.impl

import com.example.data.dao.ProjectDao
import com.example.data.dao.ProjectMemberDao
import com.example.data.dao.ProjectNoteDao
import com.example.data.model.Project
import com.example.data.model.ProjectMember
import com.example.data.model.ProjectNote
import com.example.data.model.ReportStage
import com.example.domain.repository.IProjectRepository
import kotlinx.coroutines.flow.Flow

class ProjectRepositoryImpl(
  private val projectDao: ProjectDao,
  private val memberDao: ProjectMemberDao,
  private val noteDao: ProjectNoteDao
) : IProjectRepository {

  override val activeProjectsFlow: Flow<List<Project>> = projectDao.getActiveProjectsFlow()
  override val archivedProjectsFlow: Flow<List<Project>> = projectDao.getArchivedProjectsFlow()

  override fun getProjectFlow(id: String): Flow<Project?> = projectDao.getProjectByIdFlow(id)
  override suspend fun getProject(id: String): Project? = projectDao.getProjectById(id)
  override suspend fun insertProject(project: Project) = projectDao.insertProject(project)
  override suspend fun updateProject(project: Project) = projectDao.updateProject(project)
  override suspend fun deleteProject(projectId: String) = projectDao.deleteProjectById(projectId)
  override suspend fun getActiveProjectCount(): Int = projectDao.getActiveProjectCount()

  override fun getStagesForProjectFlow(projectId: String): Flow<List<ReportStage>> =
    projectDao.getStagesForProjectFlow(projectId)

  override suspend fun getStagesForProject(projectId: String): List<ReportStage> =
    projectDao.getStagesForProject(projectId)

  override suspend fun insertStages(stages: List<ReportStage>) = projectDao.insertStages(stages)
  override suspend fun updateStage(stage: ReportStage) = projectDao.updateStage(stage)
  override suspend fun getStageById(stageId: String): ReportStage? = projectDao.getStageById(stageId)

  override fun getMembersFlow(projectId: String): Flow<List<ProjectMember>> =
    memberDao.getMembersFlow(projectId)

  override suspend fun getMembers(projectId: String): List<ProjectMember> =
    memberDao.getMembers(projectId)

  override suspend fun insertMembers(members: List<ProjectMember>) = memberDao.insertMembers(members)

  override suspend fun insertMember(member: ProjectMember) {
    if (member.isCoordinatingSupervisor) {
      memberDao.clearCoordinatingSupervisor(member.projectId)
    }
    memberDao.insertMember(member)
  }

  override suspend fun deleteMember(memberId: String) = memberDao.deleteMemberById(memberId)
  override suspend fun clearCoordinatingSupervisor(projectId: String) = memberDao.clearCoordinatingSupervisor(projectId)

  override fun getNotesFlow(projectId: String): Flow<List<ProjectNote>> =
    noteDao.getNotesFlow(projectId)

  override suspend fun getNotes(projectId: String): List<ProjectNote> =
    noteDao.getNotes(projectId)

  override suspend fun insertNote(note: ProjectNote) = noteDao.insertNote(note)
  override suspend fun deleteNote(noteId: String) = noteDao.deleteNoteById(noteId)
}
