package com.jazbass.gaboum.common.database

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jazbass.gaboum.common.entities.GameEntity

interface GameDao {

    @Query("SELECT * FROM GaboumEntity")
    fun getAllGames(): LiveData<MutableList<GameEntity>>

    @Query("SELECT * FROM GameEntity WHERE id = :id")
    fun getGameById(id: Long): LiveData<GameEntity>

    @Insert
    suspend fun addGame(gaboumEntity: GameEntity): Long

    @Update
    suspend fun updateGame(gaboumEntity: GameEntity): Int

    @Delete
    suspend fun deleteGame(gaboumEntity: GameEntity): Int
}