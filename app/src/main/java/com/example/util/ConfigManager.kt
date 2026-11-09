package com.example.util

import android.content.Context
import com.example.data.model.BuildingGroup
import org.json.JSONObject

data class ConfigStage(
  val stageNumber: Int,
  val title: String,
  val defaultChecklist: List<String>
)

object ConfigManager {

  private var cachedJson: JSONObject? = null

  private fun getOrLoadJson(context: Context): JSONObject {
    cachedJson?.let { return it }
    val jsonString = context.assets.open("supervision_stages_config.json").bufferedReader().use {
      it.readText()
    }
    val obj = JSONObject(jsonString)
    cachedJson = obj
    return obj
  }

  fun getStagesForGroup(context: Context, group: BuildingGroup): List<ConfigStage> {
    val json = getOrLoadJson(context)
    val buildingGroups = json.optJSONObject("building_groups") ?: return emptyList()
    val groupObj = buildingGroups.optJSONObject(group.code) ?: return emptyList()
    val stagesArray = groupObj.optJSONArray("stages") ?: return emptyList()

    val result = mutableListOf<ConfigStage>()
    for (i in 0 until stagesArray.length()) {
      val stageObj = stagesArray.getJSONObject(i)
      val stageNum = stageObj.getInt("stage_number")
      val title = stageObj.getString("title")
      val checklistArray = stageObj.optJSONArray("default_checklist")
      val checklist = mutableListOf<String>()
      if (checklistArray != null) {
        for (j in 0 until checklistArray.length()) {
          checklist.add(checklistArray.getString(j))
        }
      }
      result.add(ConfigStage(stageNum, title, checklist))
    }
    return result
  }

  fun getDefaultChecklistForStage(context: Context, group: BuildingGroup, stageNumber: Int): List<String> {
    val stages = getStagesForGroup(context, group)
    return stages.find { it.stageNumber == stageNumber }?.defaultChecklist ?: emptyList()
  }
}
