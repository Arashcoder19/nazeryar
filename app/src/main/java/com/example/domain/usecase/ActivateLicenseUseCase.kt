package com.example.domain.usecase

import com.example.domain.repository.IUserProfileRepository
import com.example.util.LicenseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActivateLicenseUseCase(
  private val userProfileRepository: IUserProfileRepository
) {
  suspend operator fun invoke(key: String): Boolean = withContext(Dispatchers.IO) {
    val profile = userProfileRepository.getUserProfile() ?: return@withContext false
    val isValid = LicenseManager.validateActivationKey(profile.deviceId, key)
    if (isValid) {
      userProfileRepository.updateActivation(profile.id, true, key.trim())
      true
    } else {
      false
    }
  }
}
