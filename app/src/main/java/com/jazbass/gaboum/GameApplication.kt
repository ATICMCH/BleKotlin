package com.jazbass.gaboum

import android.app.Application
import androidx.room.Room
import com.jazbass.gaboum.common.database.GameDatabase

class GameApplication : Application() {

    companion object{
        lateinit var database: GameDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            GameDatabase::class.java,
            "GaboumDatabase"
        ).build()
    }
}