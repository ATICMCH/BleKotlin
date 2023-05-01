package com.jazbass.gaboum

import androidx.room.Room
import android.app.Application
import com.jazbass.gaboum.common.database.GameDatabase

class GameApplication : Application() {

    /**/

    companion object{
        lateinit var database: GameDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            GameDatabase::class.java,
            "GameDatabase"
        ).build()
    }
}