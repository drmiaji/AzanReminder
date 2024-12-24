package com.islamic.prayertimesapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.islamic.prayertimesapp.data.models.PrayerTimeResponse

@Dao
interface PrayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePrayersTimes(prayerTimeResponse: PrayerTimeResponse): Long

    @Query("SELECT * FROM prayers")
    fun getAllPrayersTimes(): LiveData<PrayerTimeResponse>


    @Query("delete FROM prayers")
    suspend fun deleteAll()

}