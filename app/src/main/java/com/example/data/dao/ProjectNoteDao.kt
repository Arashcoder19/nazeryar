package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ProjectNote
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectNoteDao {
  @Query("SELECT * FROM project_notes WHERE projectId = :projectId ORDER BY createdAt DESC")
  fun getNotesFlow(projectId: String): Flow<List<ProjectNote>>

  @Query("SELECT * FROM project_notes WHERE projectId = :projectId ORDER BY createdAt DESC")
  suspend fun getNotes(projectId: String): List<ProjectNote>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: ProjectNote)

  @Update
  suspend fun updateNote(note: ProjectNote)

  @Query("DELETE FROM project_notes WHERE id = :noteId")
  suspend fun deleteNoteById(noteId: String)
}
