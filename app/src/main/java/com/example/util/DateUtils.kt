package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

  /**
   * Converts Gregorian year, month, day to Persian (Jalali) date (year, month, day)
   */
  fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
    val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

    val gy2 = gy - 1600
    val gm2 = gm - 1
    val gd2 = gd - 1

    var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400

    for (i in 0 until gm2) {
      gDayNo += gDaysInMonth[i + 1]
    }
    if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
      gDayNo++ // leap year
    }
    gDayNo += gd2

    var jDayNo = gDayNo - 79
    val jNp = jDayNo / 12053
    jDayNo %= 12053

    var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
    jDayNo %= 1461

    if (jDayNo >= 366) {
      jy += (jDayNo - 1) / 365
      jDayNo = (jDayNo - 1) % 365
    }

    var jm = 0
    for (i in 0 until 11) {
      if (jDayNo < jDaysInMonth[i + 1]) {
        jm = i + 1
        break
      }
      jDayNo -= jDaysInMonth[i + 1]
    }
    if (jm == 0) {
      jm = 12
    }
    val jd = jDayNo + 1

    return Triple(jy, jm, jd)
  }

  fun formatToPersianDate(timestamp: Long): String {
    val date = Date(timestamp)
    val sdfY = SimpleDateFormat("yyyy", Locale.US)
    val sdfM = SimpleDateFormat("MM", Locale.US)
    val sdfD = SimpleDateFormat("dd", Locale.US)

    val gy = sdfY.format(date).toInt()
    val gm = sdfM.format(date).toInt()
    val gd = sdfD.format(date).toInt()

    val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
    val monthName = getPersianMonthName(jm)

    return toPersianDigits("$jd $monthName $jy")
  }

  fun formatToPersianDateNumeric(timestamp: Long): String {
    val date = Date(timestamp)
    val sdfY = SimpleDateFormat("yyyy", Locale.US)
    val sdfM = SimpleDateFormat("MM", Locale.US)
    val sdfD = SimpleDateFormat("dd", Locale.US)

    val gy = sdfY.format(date).toInt()
    val gm = sdfM.format(date).toInt()
    val gd = sdfD.format(date).toInt()

    val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
    return toPersianDigits(String.format(Locale.US, "%04d/%02d/%02d", jy, jm, jd))
  }

  fun getPersianMonthName(month: Int): String {
    return when (month) {
      1 -> "فروردین"
      2 -> "اردیبهشت"
      3 -> "خرداد"
      4 -> "تیر"
      5 -> "مرداد"
      6 -> "شهریور"
      7 -> "مهر"
      8 -> "آبان"
      9 -> "آذر"
      10 -> "دی"
      11 -> "بهمن"
      12 -> "اسفند"
      else -> ""
    }
  }

  fun toPersianDigits(text: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val englishDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    var result = text
    for (i in 0..9) {
      result = result.replace(englishDigits[i], persianDigits[i])
    }
    return result
  }

  fun formatCurrency(amount: Double?): String {
    if (amount == null) return "۰ ریال"
    val formatted = String.format(Locale.US, "%,.0f", amount)
    return toPersianDigits("$formatted تومان")
  }
}
