package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectLifecycleStatus
import com.example.ui.theme.ConstructionAmber
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyRed
import com.example.util.LicenseManager

@Composable
fun StatusChip(status: ProjectLifecycleStatus, modifier: Modifier = Modifier) {
  val (bgColor, textColor) = when (status) {
    ProjectLifecycleStatus.DEFINED -> Color(0xFFE2E8F0) to Color(0xFF475569)
    ProjectLifecycleStatus.CONTRACT_SIGNED -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
    ProjectLifecycleStatus.SUPERVISION_COMMITMENT_SIGNED -> Color(0xFFE0E7FF) to Color(0xFF3730A3)
    ProjectLifecycleStatus.ACTIVE -> Color(0xFFDCFCE7) to SafetyGreen
    ProjectLifecycleStatus.TEMPORARILY_INACTIVE -> Color(0xFFFEF3C7) to ConstructionAmber
    ProjectLifecycleStatus.COMPLETED -> Color(0xFFF1F5F9) to Color(0xFF334155)
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = status.titleFa,
      style = MaterialTheme.typography.labelSmall,
      color = textColor,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun StatutoryWarningBanner(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = "هشدار قانونی",
        tint = ConstructionAmber,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "یادآوری قانونی: در صورت غیرفعال بودن کارگاه ساختمان، ارائه گزارش حداقل هر سه ماه یک‌بار به شهرداری و نظام مهندسی الزامی است.",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF92400E),
        lineHeight = 18.sp
      )
    }
  }
}

@Composable
fun LicenseActivationDialog(
  deviceId: String,
  isActivated: Boolean,
  onDismiss: () -> Unit,
  onActivate: (String) -> Unit
) {
  val context = LocalContext.current
  var inputKey by remember { mutableStateOf("") }
  val validKeyForThisDevice = remember(deviceId) { LicenseManager.generateActivationKey(deviceId) }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(
        imageVector = Icons.Default.Key,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(32.dp)
      )
    },
    title = {
      Text(
        text = if (isActivated) "وضعیت لایسنس: فعال شده" else "فعال‌سازی لایسنس نظریار",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (isActivated) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp))
              .padding(12.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafetyGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "نسخه نامحدود نظریار روی این دستگاه فعال است.",
              style = MaterialTheme.typography.bodySmall,
              color = SafetyGreen
            )
          }
        } else {
          Text(
            text = "نسخه آزمایشی به ۱ پروژه فعال محدود است. برای حذف محدودیت، کد فعال‌سازی را وارد فرمایید.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Device ID Box
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "شناسه سخت‌افزاری دستگاه (Device ID):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = deviceId,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }
            IconButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Device ID", deviceId)
                clipboard.setPrimaryClip(clip)
              },
              modifier = Modifier.testTag("copy_device_id_button")
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = "کپی شناسه")
            }
          }
        }

        if (!isActivated) {
          Spacer(modifier = Modifier.height(14.dp))
          OutlinedTextField(
            value = inputKey,
            onValueChange = { inputKey = it },
            label = { Text("کد فعال‌سازی (NZR-...)") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("activation_key_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(8.dp))
          // Convenient test button so user / tester can instantly fill and activate
          TextButton(
            onClick = { inputKey = validKeyForThisDevice },
            modifier = Modifier.testTag("generate_test_key_button")
          ) {
            Text(
              text = "دریافت کلید آفلاین این دستگاه (آزمایشگاهی)",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    },
    confirmButton = {
      if (!isActivated) {
        Button(
          onClick = { onActivate(inputKey) },
          enabled = inputKey.isNotBlank(),
          modifier = Modifier.testTag("submit_activation_button")
        ) {
          Text("ثبت و فعال‌سازی")
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("بستن")
      }
    }
  )
}
