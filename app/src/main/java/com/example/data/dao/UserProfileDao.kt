package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
  @Query("SELECT * FROM user_profile LIMIT 1")
  fun getUserProfileFlow(): Flow<UserProfile?>

  @Query("SELECT * FROM user_profile LIMIT 1")
  suspend fun getUserProfile(): UserProfile?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(profile: UserProfile)

  @Query("UPDATE user_profile SET isActivated = :isActivated, licenseKey = :licenseKey WHERE id = :profileId")
  suspend fun updateActivation(profileId: String, isActivated: Boolean, licenseKey: String?)
}
