package com.example.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Report
import com.example.ui.theme.ConstructionAmber
import com.example.ui.theme.SafetyRed

@Composable
fun CompleteProjectDialog(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("اتمام قرارداد و انتقال به آرشیو", fontWeight = FontWeight.Bold) },
    text = {
      Text("آیا از اتمام قطعی نظارت و پایان این پروژه اطمینان دارید؟\nاین عملیات وضعیت پروژه را به «پایان‌یافته» تغییر داده و به بخش آرشیو منتقل می‌کند.")
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = ConstructionAmber)
      ) {
        Text("تأیید و اتمام پروژه")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("انصراف")
      }
    }
  )
}

@Composable
fun DeleteProjectDialog(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("حذف کامل پروژه", fontWeight = FontWeight.Bold, color = SafetyRed) },
    text = {
      Text("آیا از حذف این پروژه و کلیه گزارش‌ها، مراحل مالی و یادداشت‌های مرتبط با آن اطمینان دارید؟ این عمل غیرقابل بازگشت است.")
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = SafetyRed)
      ) {
        Text("حذف دائمی")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("انصراف")
      }
    }
  )
}

@Composable
fun AddFinancialStageDialog(
  isExtraArea: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (title: String, amount: Double?, note: String?) -> Unit
) {
  var title by remember { mutableStateOf(if (isExtraArea) "دریافت وجه اضافه‌بنا" else "") }
  var amountText by remember { mutableStateOf("") }
  var note by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isExtraArea) "افزودن مرحله اضافه‌بنا" else "افزودن مرحله دریافت حق‌الزحمه",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("عنوان مرحله (مثال: قسط دوم بعد از اسکلت)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("مبلغ به تومان") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("توضیحات (اختیاری)") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amount = amountText.toDoubleOrNull()
          onConfirm(title.ifBlank { "قسط مالی" }, amount, note.ifBlank { null })
        },
        enabled = title.isNotBlank()
      ) {
        Text("ثبت مرحله مالی")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("انصراف")
      }
    }
  )
}

@Composable
fun AddMemberDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, role: String, phone: String?, isSupervisor: Boolean, isCoord: Boolean) -> Unit
) {
  var memberName by remember { mutableStateOf("") }
  var memberRole by remember { mutableStateOf("") }
  var memberPhone by remember { mutableStateOf("") }
  var isSupervisor by remember { mutableStateOf(false) }
  var isCoord by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("افزودن عضو جدید به پروژه", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = memberName,
          onValueChange = { memberName = it },
          label = { Text("نام و نام خانوادگی") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = memberRole,
          onValueChange = { memberRole = it },
          label = { Text("سمت (مثال: ناظر تأسیسات برقی، مجری)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = memberPhone,
          onValueChange = { memberPhone = it },
          label = { Text("شماره تماس") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = isSupervisor, onCheckedChange = { isSupervisor = it })
          Text("این عضو ناظر است", style = MaterialTheme.typography.bodySmall)
        }
        if (isSupervisor) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isCoord, onCheckedChange = { isCoord = it })
            Text("ناظر هماهنگ‌کننده است", style = MaterialTheme.typography.bodySmall)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(
            memberName.trim(),
            memberRole.trim(),
            memberPhone.trim().ifBlank { null },
            isSupervisor,
            isCoord
          )
        },
        enabled = memberName.isNotBlank()
      ) {
        Text("افزودن")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("انصراف")
      }
    }
  )
}

@Composable
fun AddNoteDialog(
  onDismiss: () -> Unit,
  onConfirm: (content: String) -> Unit
) {
  var noteContent by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("افزودن یادداشت نظارتی", fontWeight = FontWeight.Bold) },
    text = {
      OutlinedTextField(
        value = noteContent,
        onValueChange = { noteContent = it },
        label = { Text("متن یادداشت...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3
      )
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(noteContent.trim()) },
        enabled = noteContent.isNotBlank()
      ) {
        Text("ذخیره یادداشت")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("انصراف")
      }
    }
  )
}

@Composable
fun ExportReportPdfDialog(
  report: Report,
  onDismiss: () -> Unit,
  onConfirm: (includeAttachment: Boolean) -> Unit
) {
  var includeAttachment by remember { mutableStateOf(true) }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
    title = { Text("خروجی PDF رسمی گزارش مرحله ${report.stageNumber}", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "صفحه ۱ (فرم اداری ماده ۱۳ آیین‌نامه اجرایی ماده ۳۳) همیشه طبق ضوابط تولید می‌گردد.",
          style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = includeAttachment,
            onCheckedChange = { includeAttachment = it }
          )
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "افزودن صفحه دوم (ضمیمه عکس و GPS)",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "اختیاری - دارای نشانه «ضمیمه داخلی، جزو فرم اداری نیست»",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = { onConfirm(includeAttachment) }) {
        Text("تولید و دریافت PDF")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("بستن")
      }
    }
  )
}
