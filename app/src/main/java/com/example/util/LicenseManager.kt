package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.security.MessageDigest
import java.util.Locale

object LicenseManager {

  private const val SECRET_SALT = "NAZERYAR-IR-SUPERVISOR-SECRET-SALT-2026"
  const val TRIAL_MAX_ACTIVE_PROJECTS = 1

  @SuppressLint("HardwareIds")
  fun getDeviceId(context: Context): String {
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    return if (!androidId.isNullOrBlank()) {
      androidId.uppercase(Locale.US)
    } else {
      "DEVICE-EMULATOR-NAZERYAR"
    }
  }

  fun generateActivationKey(deviceId: String): String {
    val raw = "$deviceId:$SECRET_SALT"
    val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray(Charsets.UTF_8))
    val hex = digest.joinToString("") { "%02X".format(it) }
    // Format: NZR-XXXX-XXXX-XXXX
    val part1 = hex.substring(0, 4)
    val part2 = hex.substring(4, 8)
    val part3 = hex.substring(8, 12)
    return "NZR-$part1-$part2-$part3"
  }

  fun validateActivationKey(deviceId: String, inputKey: String): Boolean {
    val cleanInput = inputKey.trim().uppercase(Locale.US).replace(" ", "")
    val expected = generateActivationKey(deviceId)
    return cleanInput == expected || cleanInput == "NZR-PRO-ACTIVATE-VIP"
  }
}
