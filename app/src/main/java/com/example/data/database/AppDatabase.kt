package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.FinancialDao
import com.example.data.dao.ProjectDao
import com.example.data.dao.ProjectMemberDao
import com.example.data.dao.ProjectNoteDao
import com.example.data.dao.ReportDao
import com.example.data.dao.UserProfileDao
import com.example.data.model.FinancialStage
import com.example.data.model.Project
import com.example.data.model.ProjectMember
import com.example.data.model.ProjectNote
import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import com.example.data.model.ReportStage
import com.example.data.model.UserProfile

@Database(
  entities = [
    UserProfile::class,
    Project::class,
    ReportStage::class,
    Report::class,
    ReportChecklistItem::class,
    ReportPhoto::class,
    FinancialStage::class,
    ProjectMember::class,
    ProjectNote::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userProfileDao(): UserProfileDao
  abstract fun projectDao(): ProjectDao
  abstract fun reportDao(): ReportDao
  abstract fun financialDao(): FinancialDao
  abstract fun projectMemberDao(): ProjectMemberDao
  abstract fun projectNoteDao(): ProjectNoteDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "nazeryar_database.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
