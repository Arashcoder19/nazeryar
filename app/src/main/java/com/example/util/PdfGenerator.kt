package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.data.model.BuildingGroup
import com.example.data.model.Project
import com.example.data.model.Report
import com.example.data.model.ReportChecklistItem
import com.example.data.model.ReportPhoto
import com.example.data.model.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object PdfGenerator {

  // Standard A4 page size in points (72 DPI)
  private const val PAGE_WIDTH = 595
  private const val PAGE_HEIGHT = 842
  private const val MARGIN = 30f

  fun generateReportPdf(
    context: Context,
    userProfile: UserProfile,
    project: Project,
    report: Report,
    stageTitle: String,
    checklistItems: List<ReportChecklistItem>,
    photos: List<ReportPhoto>,
    includeAttachmentPage: Boolean
  ): File {
    val document = PdfDocument()

    // Page 1: Official Statutory Form
    val pageInfo1 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
    val page1 = document.startPage(pageInfo1)
    drawOfficialFormPage(page1.canvas, userProfile, project, report, stageTitle, checklistItems)
    document.finishPage(page1)

    // Page 2: Internal Attachment (Optional)
    if (includeAttachmentPage) {
      val pageInfo2 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 2).create()
      val page2 = document.startPage(pageInfo2)
      drawAttachmentPage(page2.canvas, project, report, stageTitle, photos)
      document.finishPage(page2)
    }

    // Save to file
    val dir = File(context.filesDir, "reports_pdf")
    if (!dir.exists()) dir.mkdirs()
    val fileName = "Report_${project.title.replace(" ", "_")}_Stage_${report.stageNumber}_${System.currentTimeMillis()}.pdf"
    val file = File(dir, fileName)
    val fos = FileOutputStream(file)
    document.writeTo(fos)
    fos.flush()
    fos.close()
    document.close()

    return file
  }

  private fun drawOfficialFormPage(
    canvas: Canvas,
    userProfile: UserProfile,
    project: Project,
    report: Report,
    stageTitle: String,
    checklistItems: List<ReportChecklistItem>
  ) {
    // Fill white background
    canvas.drawColor(Color.WHITE)

    // Main Outer Border
    val borderPaint = Paint().apply {
      color = Color.BLACK
      style = Paint.Style.STROKE
      strokeWidth = 1.5f
    }
    val contentWidth = PAGE_WIDTH - (2 * MARGIN)
    val outerRect = RectF(MARGIN, MARGIN, PAGE_WIDTH - MARGIN, PAGE_HEIGHT - MARGIN)
    canvas.drawRect(outerRect, borderPaint)

    var currentY = MARGIN + 10f

    // Header section
    val headerPaint = TextPaint().apply {
      color = Color.BLACK
      textSize = 12f
      isFakeBoldText = true
      textAlign = Paint.Align.CENTER
    }
    val subHeaderPaint = TextPaint().apply {
      color = Color.DKGRAY
      textSize = 8.5f
      textAlign = Paint.Align.CENTER
    }
    val metaPaint = TextPaint().apply {
      color = Color.BLACK
      textSize = 9f
      textAlign = Paint.Align.RIGHT
    }

    // Top metadata (Date & Stage Number)
    val dateStr = "تاریخ: " + DateUtils.formatToPersianDateNumeric(report.reportDate)
    val reportNoStr = "شماره گزارش: " + DateUtils.toPersianDigits(report.stageNumber.toString())
    canvas.drawText(dateStr, PAGE_WIDTH - MARGIN - 15f, currentY + 12f, metaPaint)
    val leftMetaPaint = TextPaint(metaPaint).apply { textAlign = Paint.Align.LEFT }
    canvas.drawText(reportNoStr, MARGIN + 15f, currentY + 12f, leftMetaPaint)

    // Main title
    canvas.drawText("گزارش مراحل پیشرفت عملیات ساختمان", PAGE_WIDTH / 2f, currentY + 14f, headerPaint)
    canvas.drawText("(موضوع ماده ۱۳ آیین‌نامه اجرایی ماده ۳۳ قانون نظام مهندسی و کنترل ساختمان)", PAGE_WIDTH / 2f, currentY + 28f, subHeaderPaint)
    val groupLabel = if (project.buildingGroup == BuildingGroup.AB) "(گروه الف و ب - حداقل ۱۰ مرحله گزارش)" else "(گروه ج و د - حداقل ۱۴ مرحله گزارش)"
    canvas.drawText(groupLabel, PAGE_WIDTH / 2f, currentY + 40f, subHeaderPaint)

    currentY += 48f
    canvas.drawLine(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY, borderPaint)

    // Project & Engineer Specifications Box
    val specBoxTop = currentY
    val specText = """
اینجانب ${userProfile.fullName} ناظر رشته ${userProfile.discipline.titleFa} به شماره پروانه اشتغال ${DateUtils.toPersianDigits(userProfile.licenseNumber)} و شماره عضویت ${DateUtils.toPersianDigits(userProfile.membershipNumber)} در پروژه ساختمانی متعلق به پلاک ثبتی ${DateUtils.toPersianDigits(project.registeredPlateNumber)} دارای پروانه ساختمانی شماره ${DateUtils.toPersianDigits(project.buildingPermitNumber)} واقع در ${project.fullAddress} مورخ ${DateUtils.formatToPersianDate(report.reportDate)}، گزارش مرحله ذیل را جهت صدور مفاصاحساب و اقدامات قانونی به شرح زیر تقدیم می‌دارم:
    """.trimIndent()

    val textPaint = TextPaint().apply {
      color = Color.BLACK
      textSize = 8.5f
    }
    val specLayout = createStaticLayout(specText, textPaint, (contentWidth - 20).toInt())
    canvas.save()
    canvas.translate(MARGIN + 10f, currentY + 6f)
    specLayout.draw(canvas)
    canvas.restore()

    currentY += specLayout.height + 12f
    canvas.drawLine(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY, borderPaint)

    // Section A: Safety Compliance
    val sectionHeaderPaint = TextPaint().apply {
      color = Color.BLACK
      textSize = 9.5f
      isFakeBoldText = true
      textAlign = Paint.Align.RIGHT
    }
    canvas.drawText("الف) ایمنی کارگاه و حفاظت فنی:", PAGE_WIDTH - MARGIN - 10f, currentY + 14f, sectionHeaderPaint)
    
    val safetyStatus = if (report.safetyCompliance) "رعایت شده است [☑]" else "رعایت نشده است [☒]"
    canvas.drawText(safetyStatus, PAGE_WIDTH - MARGIN - 180f, currentY + 14f, TextPaint(textPaint).apply { isFakeBoldText = true })

    val safetyNote = if (!report.safetyCompliance && !report.safetyNonComplianceNote.isNullOrBlank()) {
      "توضیحات عدم رعایت: " + report.safetyNonComplianceNote
    } else {
      "کلیه تمهیدات ایمنی و حفاظت افراد و همجواری‌ها طبق مبحث ۱۲ مقررات ملی رعایت گردیده است."
    }
    val safetyLayout = createStaticLayout(safetyNote, textPaint, (contentWidth - 20).toInt())
    canvas.save()
    canvas.translate(MARGIN + 10f, currentY + 20f)
    safetyLayout.draw(canvas)
    canvas.restore()

    currentY += safetyLayout.height + 26f
    canvas.drawLine(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY, borderPaint)

    // Section B: Plan Compliance
    canvas.drawText("ب) انطباق با نقشه‌های مصوب:", PAGE_WIDTH - MARGIN - 10f, currentY + 14f, sectionHeaderPaint)
    val planStatus = if (report.matchesApprovedPlan) "منطبق است [☑]" else "دارای مغایرت است [☒]"
    canvas.drawText(planStatus, PAGE_WIDTH - MARGIN - 180f, currentY + 14f, TextPaint(textPaint).apply { isFakeBoldText = true })

    val planNote = if (!report.matchesApprovedPlan && !report.discrepancyNote.isNullOrBlank()) {
      "شرح و مقادیر مغایرت: " + report.discrepancyNote
    } else {
      "عملیات اجرا شده تا این مرحله با نقشه‌های مصوب معماری، سازه و تأسیسات مطابقت دارد."
    }
    val planLayout = createStaticLayout(planNote, textPaint, (contentWidth - 20).toInt())
    canvas.save()
    canvas.translate(MARGIN + 10f, currentY + 20f)
    planLayout.draw(canvas)
    canvas.restore()

    currentY += planLayout.height + 26f
    canvas.drawLine(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY, borderPaint)

    // Section C: Stage Checklist
    val stageHeader = "ج) گزارش مرحله " + DateUtils.toPersianDigits(report.stageNumber.toString()) + ": " + stageTitle
    canvas.drawText(stageHeader, PAGE_WIDTH - MARGIN - 10f, currentY + 14f, sectionHeaderPaint)

    var checklistY = currentY + 22f
    val checkItemPaint = TextPaint(textPaint).apply { textSize = 8f }
    val maxItems = minOf(checklistItems.size, 6)
    for (i in 0 until maxItems) {
      val item = checklistItems[i]
      val mark = if (item.isChecked) "☑" else "☐"
      val itemLine = "$mark  ${item.itemText}"
      canvas.drawText(itemLine, PAGE_WIDTH - MARGIN - 15f, checklistY, TextPaint(checkItemPaint).apply {
        textAlign = Paint.Align.RIGHT
        if (item.isChecked) isFakeBoldText = true
      })
      checklistY += 12f
    }

    currentY = checklistY + 6f
    canvas.drawLine(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY, borderPaint)

    // Section D: Free Description
    canvas.drawText("د) شرح گزارش و دستور کارهای صادره:", PAGE_WIDTH - MARGIN - 10f, currentY + 14f, sectionHeaderPaint)
    val descText = if (report.description.isNotBlank()) report.description else "مرحله فوق بازدید و کلیه ضوابط فنی و اجرایی تحت نظارت اینجانب بررسی گردید."
    val descLayout = createStaticLayout(descText, textPaint, (contentWidth - 20).toInt())
    canvas.save()
    canvas.translate(MARGIN + 10f, currentY + 20f)
    descLayout.draw(canvas)
    canvas.restore()

    currentY += descLayout.height + 24f

    // Footer Signatures & Municipal Box
    val footerBoxTop = PAGE_HEIGHT - MARGIN - 80f
    canvas.drawLine(MARGIN, footerBoxTop, PAGE_WIDTH - MARGIN, footerBoxTop, borderPaint)
    val midX = MARGIN + (contentWidth / 2f)
    canvas.drawLine(midX, footerBoxTop, midX, PAGE_HEIGHT - MARGIN - 20f, borderPaint)

    val footerTitlePaint = TextPaint().apply {
      color = Color.BLACK
      textSize = 8.5f
      isFakeBoldText = true
      textAlign = Paint.Align.CENTER
    }
    canvas.drawText("محل ثبت دبیرخانه شهرداری / نظام مهندسی", MARGIN + (contentWidth * 0.25f), footerBoxTop + 16f, footerTitlePaint)
    canvas.drawText("مهر و امضای مهندس ناظر", MARGIN + (contentWidth * 0.75f), footerBoxTop + 16f, footerTitlePaint)
    canvas.drawText(userProfile.fullName, MARGIN + (contentWidth * 0.75f), footerBoxTop + 40f, TextPaint(textPaint).apply { textAlign = Paint.Align.CENTER })

    // Statutory Warning at the bottom
    val noteTop = PAGE_HEIGHT - MARGIN - 18f
    canvas.drawLine(MARGIN, noteTop, PAGE_WIDTH - MARGIN, noteTop, borderPaint)
    val warningPaint = TextPaint().apply {
      color = Color.RED
      textSize = 7.5f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
    }
    canvas.drawText("* تذکر مهم قانونی: در صورت غیرفعال بودن کارگاه ساختمان، ارائه گزارش حداقل هر سه ماه یک‌بار ضروری است.", PAGE_WIDTH / 2f, noteTop + 12f, warningPaint)
  }

  private fun drawAttachmentPage(
    canvas: Canvas,
    project: Project,
    report: Report,
    stageTitle: String,
    photos: List<ReportPhoto>
  ) {
    canvas.drawColor(Color.WHITE)

    val borderPaint = Paint().apply {
      color = Color.BLACK
      style = Paint.Style.STROKE
      strokeWidth = 1.5f
    }
    val contentWidth = PAGE_WIDTH - (2 * MARGIN)
    val outerRect = RectF(MARGIN, MARGIN, PAGE_WIDTH - MARGIN, PAGE_HEIGHT - MARGIN)
    canvas.drawRect(outerRect, borderPaint)

    var currentY = MARGIN + 14f

    // Header
    val headerPaint = TextPaint().apply {
      color = Color.BLACK
      textSize = 13f
      isFakeBoldText = true
      textAlign = Paint.Align.CENTER
    }
    val subHeaderPaint = TextPaint().apply {
      color = Color.DKGRAY
      textSize = 9.5f
      textAlign = Paint.Align.CENTER
    }

    canvas.drawText("ضمیمه گزارش مرحله " + DateUtils.toPersianDigits(report.stageNumber.toString()), PAGE_WIDTH / 2f, currentY + 12f, headerPaint)
    canvas.drawText("پروژه: " + project.title + " | " + stageTitle, PAGE_WIDTH / 2f, currentY + 28f, subHeaderPaint)

    currentY += 40f
    canvas.drawLine(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY, borderPaint)

    // Photos section
    val sectionHeaderPaint = TextPaint().apply {
      color = Color.BLACK
      textSize = 10f
      isFakeBoldText = true
      textAlign = Paint.Align.RIGHT
    }
    canvas.drawText("تصاویر پیوست کارگاه:", PAGE_WIDTH - MARGIN - 15f, currentY + 16f, sectionHeaderPaint)
    currentY += 26f

    if (photos.isNotEmpty()) {
      val maxPhotos = minOf(photos.size, 2)
      val photoWidth = (contentWidth - 30f) / 2f
      val photoHeight = 180f

      for (i in 0 until maxPhotos) {
        val p = photos[i]
        val left = MARGIN + 10f + (i * (photoWidth + 10f))
        val rect = RectF(left, currentY, left + photoWidth, currentY + photoHeight)

        runCatching {
          val bitmap = BitmapFactory.decodeFile(p.filePath)
          if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, rect, null)
          } else {
            drawPlaceholderPhoto(canvas, rect, "تصویر ${i + 1}")
          }
        }.onFailure {
          drawPlaceholderPhoto(canvas, rect, "تصویر ${i + 1}")
        }
      }
      currentY += photoHeight + 15f
    } else {
      val noPhotoPaint = TextPaint().apply {
        color = Color.GRAY
        textSize = 9f
        textAlign = Paint.Align.CENTER
      }
      canvas.drawText("تصویری برای این گزارش ثبت نشده است.", PAGE_WIDTH / 2f, currentY + 30f, noPhotoPaint)
      currentY += 60f
    }

    canvas.drawLine(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY, borderPaint)

    // GPS & Location section
    currentY += 15f
    canvas.drawText("موقعیت مکانی و مختصات جغرافیایی (GPS):", PAGE_WIDTH - MARGIN - 15f, currentY, sectionHeaderPaint)
    currentY += 16f

    val textPaint = TextPaint().apply {
      color = Color.BLACK
      textSize = 9f
      textAlign = Paint.Align.RIGHT
    }

    if (report.gpsLat != null && report.gpsLng != null) {
      val latStr = String.format(Locale.US, "%.6f", report.gpsLat)
      val lngStr = String.format(Locale.US, "%.6f", report.gpsLng)
      canvas.drawText("عرض جغرافیایی (Latitude): $latStr", PAGE_WIDTH - MARGIN - 20f, currentY, textPaint)
      currentY += 14f
      canvas.drawText("طول جغرافیایی (Longitude): $lngStr", PAGE_WIDTH - MARGIN - 20f, currentY, textPaint)
      currentY += 14f
      canvas.drawText("پیوند مسیریابی مستقیم:", PAGE_WIDTH - MARGIN - 20f, currentY, textPaint)
      currentY += 14f
      val linkPaint = TextPaint().apply {
        color = Color.BLUE
        textSize = 8.5f
        textAlign = Paint.Align.RIGHT
      }
      val mapLink = "https://maps.google.com/?q=${report.gpsLat},${report.gpsLng}"
      canvas.drawText(mapLink, PAGE_WIDTH - MARGIN - 20f, currentY, linkPaint)
    } else {
      canvas.drawText("مختصات GPS در زمان ثبت گزارش دریافت نشده است.", PAGE_WIDTH - MARGIN - 20f, currentY, textPaint)
    }

    // Generated timestamp
    val metaBottom = PAGE_HEIGHT - MARGIN - 36f
    canvas.drawLine(MARGIN, metaBottom, PAGE_WIDTH - MARGIN, metaBottom, borderPaint)
    val dateInfo = "تاریخ و زمان تولید این سند: " + DateUtils.formatToPersianDate(System.currentTimeMillis())
    canvas.drawText(dateInfo, PAGE_WIDTH - MARGIN - 15f, metaBottom + 12f, textPaint)

    // Statutory Warning at bottom of Page 2 (Mandatory per Section 7 specs)
    val disclaimerPaint = TextPaint().apply {
      color = Color.DKGRAY
      textSize = 8f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
    }
    canvas.drawText("این صفحه ضمیمه‌ی داخلی است و جزو فرم رسمی نظام مهندسی نیست.", PAGE_WIDTH / 2f, PAGE_HEIGHT - MARGIN - 8f, disclaimerPaint)
  }

  private fun drawPlaceholderPhoto(canvas: Canvas, rect: RectF, label: String) {
    val fillPaint = Paint().apply {
      color = Color.LTGRAY
      style = Paint.Style.FILL
    }
    val strokePaint = Paint().apply {
      color = Color.GRAY
      style = Paint.Style.STROKE
    }
    val textPaint = TextPaint().apply {
      color = Color.DKGRAY
      textSize = 10f
      textAlign = Paint.Align.CENTER
    }
    canvas.drawRect(rect, fillPaint)
    canvas.drawRect(rect, strokePaint)
    canvas.drawText(label, rect.centerX(), rect.centerY(), textPaint)
  }

  private fun createStaticLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(2f, 1f)
        .setIncludePad(true)
        .build()
    } else {
      @Suppress("DEPRECATION")
      StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1f, 2f, true)
    }
  }
}
