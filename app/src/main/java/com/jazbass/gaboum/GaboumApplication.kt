package com.jazbass.gaboum

import android.app.Application
import androidx.room.Room
import com.jazbass.gaboum.common.database.GaboumDatabase

class GaboumApplication : Application() {

    companion object{
        lateinit var database: GaboumDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            GaboumDatabase::class.java,
            "GaboumDatabase"
        ).build()
    }
}