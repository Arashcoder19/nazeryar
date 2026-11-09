package com.example.ui.screens.report_wizard

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ui.theme.SafetyGreen
import java.io.File

@Composable
fun ReportStepPhotosGps(
  photoPaths: SnapshotStateList<String>,
  maxPhotosLimit: Int,
  gpsLat: Double?,
  gpsLng: Double?,
  markStageCompleted: Boolean,
  onRequestLocation: () -> Unit,
  onPickPhotos: () -> Unit,
  onToggleMarkCompleted: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "پیوست تصاویر و موقعیت جغرافیایی (GPS)",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "تصاویر و GPS در صفحه دوم (ضمیمه اختیاری) ثبت می‌شوند و در فرم اداری ادغام نخواهند شد.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(14.dp))

    // GPS Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(10.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "مختصات محل پروژه (GPS)", fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onRequestLocation,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("دریافت موقعیت", fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (gpsLat != null && gpsLng != null) {
          Text(
            text = "عرض: $gpsLat | طول: $gpsLng",
            style = MaterialTheme.typography.bodyMedium,
            color = SafetyGreen,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedButton(
            onClick = {
              val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$gpsLat,$gpsLng?q=$gpsLat,$gpsLng"))
              runCatching { context.startActivity(mapIntent) }
            },
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("نمایش در نشان / بلد / گوگل مپ", fontSize = 12.sp)
          }
        } else {
          Text(
            text = "مختصات ثبت نشده است (در صورت لزوم روی دریافت موقعیت لمس کنید).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Photos Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(10.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "عکس‌های کارگاه (${photoPaths.size} از $maxPhotosLimit)",
            fontWeight = FontWeight.Bold
          )

          if (photoPaths.size < maxPhotosLimit) {
            OutlinedButton(
              onClick = onPickPhotos,
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("انتخاب عکس", fontSize = 12.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (photoPaths.isEmpty()) {
          Text(
            text = "هیچ تصویری پیوست نشده است.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            photoPaths.forEachIndexed { idx, path ->
              Box(
                modifier = Modifier
                  .size(90.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
              ) {
                Image(
                  painter = rememberAsyncImagePainter(File(path)),
                  contentDescription = "عکس $idx",
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop
                )
                IconButton(
                  onClick = { photoPaths.removeAt(idx) },
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                  Icon(Icons.Default.Close, contentDescription = "حذف", tint = Color.White, modifier = Modifier.size(14.dp))
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Mark stage completed toggle
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = "علامت‌زدن این مرحله به عنوان «تکمیل‌شده»", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Text(text = "در لیست مراحل با تیک سبز مشخص می‌شود.", style = MaterialTheme.typography.bodySmall)
      }
      Switch(checked = markStageCompleted, onCheckedChange = onToggleMarkCompleted)
    }
  }
}
