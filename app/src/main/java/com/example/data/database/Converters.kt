package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.BuildingGroup
import com.example.data.model.EngineerDiscipline
import com.example.data.model.EngineerGrade
import com.example.data.model.ProjectLifecycleStatus

class Converters {
  @TypeConverter
  fun fromEngineerGrade(grade: EngineerGrade): String = grade.name

  @TypeConverter
  fun toEngineerGrade(value: String): EngineerGrade =
    runCatching { EngineerGrade.valueOf(value) }.getOrDefault(EngineerGrade.GRADE_3)

  @TypeConverter
  fun fromEngineerDiscipline(discipline: EngineerDiscipline): String = discipline.name

  @TypeConverter
  fun toEngineerDiscipline(value: String): EngineerDiscipline =
    runCatching { EngineerDiscipline.valueOf(value) }.getOrDefault(EngineerDiscipline.CIVIL)

  @TypeConverter
  fun fromBuildingGroup(group: BuildingGroup): String = group.name

  @TypeConverter
  fun toBuildingGroup(value: String): BuildingGroup =
    runCatching { BuildingGroup.valueOf(value) }.getOrDefault(BuildingGroup.AB)

  @TypeConverter
  fun fromProjectLifecycleStatus(status: ProjectLifecycleStatus): String = status.name

  @TypeConverter
  fun toProjectLifecycleStatus(value: String): ProjectLifecycleStatus =
    runCatching { ProjectLifecycleStatus.valueOf(value) }.getOrDefault(ProjectLifecycleStatus.ACTIVE)
}
