package com.example.util

import android.content.Context
import com.example.data.model.FinancialStage
import com.example.data.model.Project
import com.example.data.model.ProjectMember
import com.example.data.model.ProjectNote
import com.example.data.model.Report
import com.example.data.model.ReportStage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExcelExportManager {

  fun exportProjectArchiveZip(
    context: Context,
    project: Project,
    stages: List<ReportStage>,
    reports: List<Report>,
    financials: List<FinancialStage>,
    members: List<ProjectMember>,
    notes: List<ProjectNote>
  ): File {
    val dir = File(context.filesDir, "project_archives")
    if (!dir.exists()) dir.mkdirs()

    val zipFile = File(dir, "Archive_${project.title.replace(" ", "_")}_${System.currentTimeMillis()}.zip")
    val zos = ZipOutputStream(FileOutputStream(zipFile))

    // 1. Reports CSV (UTF-8 with BOM for Excel compatibility)
    val reportsCsv = buildString {
      append("\uFEFF") // UTF-8 BOM
      append("شماره مرحله,عنوان مرحله,تاریخ گزارش,رعایت ایمنی,انطباق با نقشه,مختصات GPS,شرح گزارش\n")
      for (r in reports) {
        val stageTitle = stages.find { it.id == r.stageId }?.stageTitle ?: "مرحله ${r.stageNumber}"
        val dateFa = DateUtils.formatToPersianDateNumeric(r.reportDate)
        val safety = if (r.safetyCompliance) "رعایت شده" else "عدم رعایت (${r.safetyNonComplianceNote ?: ""})"
        val plan = if (r.matchesApprovedPlan) "منطبق" else "مغایرت (${r.discrepancyNote ?: ""})"
        val gps = if (r.gpsLat != null) "${r.gpsLat},${r.gpsLng}" else "ثبت نشده"
        val desc = r.description.replace(",", " - ").replace("\n", " ")
        append("${r.stageNumber},\"$stageTitle\",$dateFa,\"$safety\",\"$plan\",\"$gps\",\"$desc\"\n")
      }
    }
    addTextEntryToZip(zos, "گزارش_های_فنی.csv", reportsCsv)

    // 2. Financials CSV
    val financialCsv = buildString {
      append("\uFEFF")
      append("عنوان مرحله مالی,مبلغ (ریال),اضافه بنا,وضعیت دریافت,تاریخ دریافت,توضیحات\n")
      for (f in financials) {
        val amount = f.amount?.toString() ?: "0"
        val extra = if (f.isExtraArea) "بله" else "خیر"
        val status = if (f.isReceived) "دریافت شده" else "در انتظار دریافت"
        val dateFa = if (f.receivedDate != null) DateUtils.formatToPersianDateNumeric(f.receivedDate) else "-"
        val note = (f.note ?: "").replace(",", " - ").replace("\n", " ")
        append("\"${f.title}\",$amount,$extra,$status,$dateFa,\"$note\"\n")
      }
    }
    addTextEntryToZip(zos, "مراحل_مالی.csv", financialCsv)

    // 3. Members CSV
    val membersCsv = buildString {
      append("\uFEFF")
      append("نام و نام خانوادگی,سمت / نقش,شماره تماس,ناظر است,ناظر هماهنگ‌کننده\n")
      for (m in members) {
        val sup = if (m.isSupervisor) "بله" else "خیر"
        val coord = if (m.isCoordinatingSupervisor) "بله" else "خیر"
        append("\"${m.fullName}\",\"${m.role}\",\"${m.phoneNumber ?: ""}\",$sup,$coord\n")
      }
    }
    addTextEntryToZip(zos, "اعضای_پروژه.csv", membersCsv)

    // 4. Notes CSV
    val notesCsv = buildString {
      append("\uFEFF")
      append("تاریخ ثبت,متن یادداشت\n")
      for (n in notes) {
        val dateFa = DateUtils.formatToPersianDateNumeric(n.createdAt)
        val content = n.content.replace("\"", "\"\"")
        append("$dateFa,\"$content\"\n")
      }
    }
    addTextEntryToZip(zos, "یادداشت_ها.csv", notesCsv)

    // 5. Existing PDF files
    for (r in reports) {
      if (!r.pdfFilePath.isNullOrBlank()) {
        val pdfFile = File(r.pdfFilePath)
        if (pdfFile.exists()) {
          addFileToZip(zos, "PDF_گزارش_ها/${pdfFile.name}", pdfFile)
        }
      }
    }

    zos.flush()
    zos.close()
    return zipFile
  }

  private fun addTextEntryToZip(zos: ZipOutputStream, entryName: String, text: String) {
    val entry = ZipEntry(entryName)
    zos.putNextEntry(entry)
    val bytes = text.toByteArray(StandardCharsets.UTF_8)
    zos.write(bytes)
    zos.closeEntry()
  }

  private fun addFileToZip(zos: ZipOutputStream, entryName: String, file: File) {
    runCatching {
      val entry = ZipEntry(entryName)
      zos.putNextEntry(entry)
      file.inputStream().use { it.copyTo(zos) }
      zos.closeEntry()
    }
  }
}
