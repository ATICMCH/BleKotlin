package com.jazbass.gaboum.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.jazbass.gaboum.common.entities.PlayerEntity

@Dao
interface PlayerDao {

    @Insert
    fun insert(playerEntity: PlayerEntity)

    @Query("SELECT * FROM PlayerEntity")
    fun getAllUsers(): List<PlayerEntity>

    @Query("SELECT * FROM PlayerEntity WHERE game_id = :gameId")
    fun getUserByGameId(gameId: Long): LiveData<List<PlayerEntity>>


}