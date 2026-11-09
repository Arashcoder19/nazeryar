package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
  @PrimaryKey val id: String = "primary_user_profile",
  val firstName: String,
  val lastName: String,
  val membershipNumber: String,
  val licenseNumber: String,
  val grade: EngineerGrade,
  val discipline: EngineerDiscipline,
  val isActivated: Boolean = false,
  val licenseKey: String? = null,
  val deviceId: String,
  val createdAt: Long = System.currentTimeMillis()
) {
  val fullName: String
    get() = "$firstName $lastName".trim()
}

@Entity(tableName = "projects")
data class Project(
  @PrimaryKey val id: String,
  val title: String,
  val province: String,
  val city: String,
  val municipalityZone: String,
  val street: String,
  val alley: String,
  val plateNumber: String,
  val registeredPlateNumber: String,
  val buildingPermitNumber: String,
  val buildingGroup: BuildingGroup,
  val lifecycleStatus: ProjectLifecycleStatus = ProjectLifecycleStatus.ACTIVE,
  val contractSignedDate: Long? = null,
  val supervisionCommitmentDate: Long? = null,
  val completedDate: Long? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  val fullAddress: String
    get() = buildString {
      append(province).append("، ").append(city)
      if (municipalityZone.isNotBlank()) append("، منطقه ").append(municipalityZone)
      if (street.isNotBlank()) append("، ").append(street)
      if (alley.isNotBlank()) append("، ").append(alley)
      if (plateNumber.isNotBlank()) append("، پلاک ").append(plateNumber)
    }
}

@Entity(
  tableName = "report_stages",
  foreignKeys = [
    ForeignKey(
      entity = Project::class,
      parentColumns = ["id"],
      childColumns = ["projectId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["projectId"]), Index(value = ["projectId", "stageNumber"])]
)
data class ReportStage(
  @PrimaryKey val id: String,
  val projectId: String,
  val stageNumber: Int,
  val stageTitle: String,
  val isCompleted: Boolean = false,
  val defaultChecklistTemplateId: String = ""
)

@Entity(
  tableName = "reports",
  foreignKeys = [
    ForeignKey(
      entity = Project::class,
      parentColumns = ["id"],
      childColumns = ["projectId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = ReportStage::class,
      parentColumns = ["id"],
      childColumns = ["stageId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["projectId"]), Index(value = ["stageId"])]
)
data class Report(
  @PrimaryKey val id: String,
  val projectId: String,
  val stageId: String,
  val stageNumber: Int,
  val reportDate: Long = System.currentTimeMillis(),
  val safetyCompliance: Boolean = true,
  val safetyNonComplianceNote: String? = null,
  val matchesApprovedPlan: Boolean = true,
  val discrepancyNote: String? = null,
  val stageReportTypeChecked: String = "",
  val description: String = "",
  val gpsLat: Double? = null,
  val gpsLng: Double? = null,
  val pdfFilePath: String? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "report_checklist_items",
  foreignKeys = [
    ForeignKey(
      entity = Report::class,
      parentColumns = ["id"],
      childColumns = ["reportId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["reportId"])]
)
data class ReportChecklistItem(
  @PrimaryKey val id: String,
  val reportId: String,
  val itemText: String,
  val isChecked: Boolean = false,
  val sortOrder: Int = 0,
  val isCustom: Boolean = false
)

@Entity(
  tableName = "report_photos",
  foreignKeys = [
    ForeignKey(
      entity = Report::class,
      parentColumns = ["id"],
      childColumns = ["reportId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["reportId"])]
)
data class ReportPhoto(
  @PrimaryKey val id: String,
  val reportId: String,
  val filePath: String,
  val sortOrder: Int = 0,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "financial_stages",
  foreignKeys = [
    ForeignKey(
      entity = Project::class,
      parentColumns = ["id"],
      childColumns = ["projectId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["projectId"])]
)
data class FinancialStage(
  @PrimaryKey val id: String,
  val projectId: String,
  val title: String,
  val sortOrder: Int = 0,
  val isExtraArea: Boolean = false,
  val amount: Double? = null,
  val receivedDate: Long? = null,
  val isReceived: Boolean = false,
  val note: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "project_members",
  foreignKeys = [
    ForeignKey(
      entity = Project::class,
      parentColumns = ["id"],
      childColumns = ["projectId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["projectId"])]
)
data class ProjectMember(
  @PrimaryKey val id: String,
  val projectId: String,
  val fullName: String,
  val role: String,
  val phoneNumber: String? = null,
  val isSupervisor: Boolean = false,
  val isCoordinatingSupervisor: Boolean = false,
  val isAppUser: Boolean = false,
  val sortOrder: Int = 0
)

@Entity(
  tableName = "project_notes",
  foreignKeys = [
    ForeignKey(
      entity = Project::class,
      parentColumns = ["id"],
      childColumns = ["projectId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["projectId"])]
)
data class ProjectNote(
  @PrimaryKey val id: String,
  val projectId: String,
  val content: String,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)
