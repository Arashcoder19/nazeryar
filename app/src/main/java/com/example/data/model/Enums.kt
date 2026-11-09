package com.example.data.model

enum class EngineerGrade(val titleFa: String) {
  GRADE_1("پایه ۱"),
  GRADE_2("پایه ۲"),
  GRADE_3("پایه ۳"),
  SENIOR("ارشد");

  companion object {
    fun fromString(value: String): EngineerGrade =
      entries.find { it.name == value || it.titleFa == value } ?: GRADE_3
  }
}

enum class EngineerDiscipline(val titleFa: String) {
  ARCHITECTURE("معماری"),
  CIVIL("عمران"),
  MECHANICAL("تأسیسات مکانیکی"),
  ELECTRICAL("تأسیسات برقی"),
  URBAN_PLANNING("شهرسازی"),
  SURVEYING("نقشه‌برداری");

  companion object {
    fun fromString(value: String): EngineerDiscipline =
      entries.find { it.name == value || it.titleFa == value } ?: CIVIL
  }
}

enum class BuildingGroup(val code: String, val titleFa: String, val stageCount: Int) {
  AB("AB", "گروه الف و ب (۱۰ مرحله)", 10),
  CD("CD", "گروه ج و د (۱۴ مرحله)", 14);

  companion object {
    fun fromCode(code: String): BuildingGroup =
      entries.find { it.code.equals(code, ignoreCase = true) } ?: AB
  }
}

enum class ProjectLifecycleStatus(val titleFa: String) {
  DEFINED("تعریف شده"),
  CONTRACT_SIGNED("قرارداد منعقد شده"),
  SUPERVISION_COMMITMENT_SIGNED("تعهد نظارت ثبت شده"),
  ACTIVE("فعال"),
  TEMPORARILY_INACTIVE("غیرفعال موقت"),
  COMPLETED("پایان‌یافته (آرشیو)");

  val isArchived: Boolean
    get() = this == COMPLETED
}
