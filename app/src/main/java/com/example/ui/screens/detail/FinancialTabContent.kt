package com.example.ui.screens.detail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialStage
import com.example.ui.theme.ConstructionAmber
import com.example.ui.theme.SafetyGreen
import com.example.util.DateUtils

@Composable
fun FinancialTabContent(
  financials: List<FinancialStage>,
  onAddStandardStage: () -> Unit,
  onAddExtraAreaStage: () -> Unit,
  onToggleReceived: (FinancialStage, Boolean) -> Unit,
  onDeleteStage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val totalReceived = financials.filter { it.isReceived }.mapNotNull { it.amount }.sum()
  val totalPending = financials.filter { !it.isReceived }.mapNotNull { it.amount }.sum()

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    // Financial Summary Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      shape = RoundedCornerShape(12.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "مجموع دریافتی‌ها",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = DateUtils.formatCurrency(totalReceived),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SafetyGreen
          )
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "در انتظار دریافت",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = DateUtils.formatCurrency(totalPending),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ConstructionAmber
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Action buttons
    Row(modifier = Modifier.fillMaxWidth()) {
      Button(
        onClick = onAddStandardStage,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("مرحله دریافت وجه", fontSize = 12.sp)
      }
      Spacer(modifier = Modifier.width(8.dp))
      OutlinedButton(
        onClick = onAddExtraAreaStage,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("دریافت اضافه‌بنا", fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (financials.isEmpty()) {
      Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
          text = "هنوز مرحله مالی تعریف نشده است.\nمسیر مالی کاملاً مستقل از گزارش‌های فنی مدیریت می‌شود.",
          style = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(financials, key = { it.id }) { stage ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = stage.isReceived,
                onCheckedChange = { checked -> onToggleReceived(stage, checked) }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = stage.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                  )
                  if (stage.isExtraArea) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "اضافه‌بنا",
                        color = ConstructionAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
                Text(
                  text = DateUtils.formatCurrency(stage.amount),
                  style = MaterialTheme.typography.bodySmall,
                  color = if (stage.isReceived) SafetyGreen else MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.SemiBold
                )
                if (stage.isReceived && stage.receivedDate != null) {
                  Text(
                    text = "تاریخ دریافت: " + DateUtils.formatToPersianDate(stage.receivedDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              IconButton(onClick = { onDeleteStage(stage.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray)
              }
            }
          }
        }
      }
    }
  }
}
