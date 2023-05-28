package com.example.busyday.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ActivityDao {
    @Insert
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("SELECT * FROM activities")
    suspend fun getAllActivities(): List<ActivityEntity>

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    @Query("SELECT * FROM activities WHERE id IN (:ids)")
    suspend fun getActivitiesByIds(ids: List<Long>): List<ActivityEntity>
}
