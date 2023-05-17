package com.jazbass.gaboum.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jazbass.gaboum.common.entities.GameEntity

@Database(entities = [GameEntity::class], version = 1)
abstract class GameDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    abstract fun playerDao(): PlayerDao
}