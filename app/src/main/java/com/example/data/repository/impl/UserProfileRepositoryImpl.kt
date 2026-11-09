package com.example.data.repository.impl

import com.example.data.dao.UserProfileDao
import com.example.data.model.UserProfile
import com.example.domain.repository.IUserProfileRepository
import kotlinx.coroutines.flow.Flow

class UserProfileRepositoryImpl(
  private val userProfileDao: UserProfileDao
) : IUserProfileRepository {

  override val userProfileFlow: Flow<UserProfile?> = userProfileDao.getUserProfileFlow()

  override suspend fun getUserProfile(): UserProfile? = userProfileDao.getUserProfile()

  override suspend fun saveUserProfile(profile: UserProfile) =
    userProfileDao.insertOrUpdate(profile)

  override suspend fun updateActivation(profileId: String, isActivated: Boolean, licenseKey: String?) =
    userProfileDao.updateActivation(profileId, isActivated, licenseKey)
}
