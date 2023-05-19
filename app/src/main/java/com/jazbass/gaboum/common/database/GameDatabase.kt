package com.jazbass.gaboum.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.common.entities.PlayerEntity

@Database(entities = [GameEntity::class, PlayerEntity::class], version = 1)
abstract class GameDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    abstract fun playerDao(): PlayerDao
}