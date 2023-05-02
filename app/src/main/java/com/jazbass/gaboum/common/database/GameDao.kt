package com.jazbass.gaboum.common.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jazbass.gaboum.common.entities.GameEntity

@Dao
interface GameDao {
    @Query("SELECT * FROM GameEntity")
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