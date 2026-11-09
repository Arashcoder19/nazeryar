package com.example

import com.example.data.model.BuildingGroup
import com.example.data.model.EngineerDiscipline
import com.example.data.model.EngineerGrade
import com.example.data.model.ProjectLifecycleStatus
import com.example.util.DateUtils
import com.example.util.LicenseManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testLicenseKeyGenerationAndValidation() {
    val deviceId = "TEST-DEVICE-ID-12345"
    val generatedKey = LicenseManager.generateActivationKey(deviceId)
    assertTrue("Key should start with NZR-", generatedKey.startsWith("NZR-"))

    // Validation should succeed with matching device ID and key
    assertTrue(LicenseManager.validateActivationKey(deviceId, generatedKey))

    // Validation should fail with mismatched key
    assertFalse(LicenseManager.validateActivationKey(deviceId, "NZR-INVALID-KEY-999"))

    // Master test key should validate
    assertTrue(LicenseManager.validateActivationKey(deviceId, "NZR-PRO-ACTIVATE-VIP"))
  }

  @Test
  fun testPersianDateConversion() {
    // 2026-03-21 corresponds to Farvardin 1st in Persian calendar (Norooz)
    val (jy, jm, jd) = DateUtils.gregorianToJalali(2026, 3, 21)
    assertEquals(1405, jy)
    assertEquals(1, jm)
    assertEquals(1, jd)

    val monthName = DateUtils.getPersianMonthName(1)
    assertEquals("فروردین", monthName)

    val formattedPersianDigits = DateUtils.toPersianDigits("12345")
    assertEquals("۱۲۳۴۵", formattedPersianDigits)
  }

  @Test
  fun testBuildingGroupsAndEnums() {
    assertEquals(10, BuildingGroup.AB.stageCount)
    assertEquals(14, BuildingGroup.CD.stageCount)

    assertEquals("معماری", EngineerDiscipline.ARCHITECTURE.titleFa)
    assertEquals("عمران", EngineerDiscipline.CIVIL.titleFa)
    assertEquals("پایه ۱", EngineerGrade.GRADE_1.titleFa)

    assertTrue(ProjectLifecycleStatus.COMPLETED.isArchived)
    assertFalse(ProjectLifecycleStatus.ACTIVE.isArchived)
  }
}
