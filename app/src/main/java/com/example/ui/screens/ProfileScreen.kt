package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.ConstructionAmber
import com.example.ui.theme.SafetyGreen
import com.example.util.DateUtils

@Composable
fun ProfileScreen(
  userProfile: UserProfile?,
  onEditProfile: () -> Unit,
  onOpenLicenseDialog: () -> Unit
) {
  if (userProfile == null) return

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Avatar & Name Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(16.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Engineering,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(42.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = userProfile.fullName,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "ناظر پایه ${userProfile.grade.titleFa} | رشته ${userProfile.discipline.titleFa}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(text = "شماره عضویت نظام مهندسی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = DateUtils.toPersianDigits(userProfile.membershipNumber), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(text = "شماره پروانه اشتغال", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = DateUtils.toPersianDigits(userProfile.licenseNumber), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
          onClick = onEditProfile,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("edit_profile_button"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("ویرایش اطلاعات پروانه مهندسی")
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // License Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(
        containerColor = if (userProfile.isActivated) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
      ),
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        if (userProfile.isActivated) SafetyGreen.copy(alpha = 0.5f) else ConstructionAmber.copy(alpha = 0.5f)
      )
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (userProfile.isActivated) Icons.Default.CheckCircle else Icons.Default.Key,
              contentDescription = null,
              tint = if (userProfile.isActivated) SafetyGreen else ConstructionAmber
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (userProfile.isActivated) "وضعیت لایسنس: فعال (نامحدود)" else "وضعیت: نسخه آزمایشی (۱ پروژه)",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = if (userProfile.isActivated) SafetyGreen else ConstructionAmber
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = if (userProfile.isActivated) {
            "تمامی قابلیت‌های نظارتی، گزارش‌دهی و خروجی‌های PDF بدون محدودیت فعال است."
          } else {
            "در نسخه آزمایشی مجاز به تعریف یک پروژه فعال هستید. جهت پروژه‌های نامحدود، کد فعال‌سازی را وارد کنید."
          },
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = onOpenLicenseDialog,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("manage_license_button"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = if (userProfile.isActivated) "مشاهده مشخصات لایسنس" else "فعال‌سازی لایسنس آفلاین")
        }
      }
    }
  }
}
