package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ProjectMember
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectMemberDao {
  @Query("SELECT * FROM project_members WHERE projectId = :projectId ORDER BY sortOrder ASC")
  fun getMembersFlow(projectId: String): Flow<List<ProjectMember>>

  @Query("SELECT * FROM project_members WHERE projectId = :projectId ORDER BY sortOrder ASC")
  suspend fun getMembers(projectId: String): List<ProjectMember>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMembers(members: List<ProjectMember>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: ProjectMember)

  @Update
  suspend fun updateMember(member: ProjectMember)

  @Query("DELETE FROM project_members WHERE id = :memberId")
  suspend fun deleteMemberById(memberId: String)

  @Query("UPDATE project_members SET isCoordinatingSupervisor = 0 WHERE projectId = :projectId")
  suspend fun clearCoordinatingSupervisor(projectId: String)
}
