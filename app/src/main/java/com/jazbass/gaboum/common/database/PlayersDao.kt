package com.jazbass.gaboum.common.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jazbass.gaboum.common.entities.PlayerEntity

@Dao
interface PlayerDao {

    @Insert
    fun insert(playerEntity: PlayerEntity)

    @Query("SELECT * FROM player")
    fun getAllUsers(gameId: Long): List<PlayerEntity>


    @Query("SELECT * FROM player WHERE gameId = :gameId")
    fun getUserByGameId(gameId: Long): List<PlayerEntity>
}