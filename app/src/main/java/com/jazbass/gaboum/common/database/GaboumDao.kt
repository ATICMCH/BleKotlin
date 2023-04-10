package com.jazbass.gaboum.common.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jazbass.gaboum.common.entities.GaboumEntity

interface GaboumDao {

    @Query("SELECT * FROM GaboumEntity")
    suspend fun getAllGames(): LiveData<MutableList<GaboumEntity>>

    @Query("SELECT * FROM GameEntity WHERE id = :id")
    suspend fun getGame(): LiveData<GaboumEntity>

    @Insert
    suspend fun addGame(gaboumEntity: GaboumEntity) : Long

    @Update
    suspend fun updateGame(gaboumEntity: GaboumEntity) : Int

    @Delete
    suspend fun deleteGame(gaboumEntity: GaboumEntity) : Int
}