package com.example.ui.screens.report_wizard

import java.util.UUID

data class EditableChecklistItem(
  val id: String = UUID.randomUUID().toString(),
  val itemText: String,
  val isChecked: Boolean = false,
  val isCustom: Boolean = false
)
