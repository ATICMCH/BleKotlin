package com.jazbass.gaboum.gameModule.model

import androidx.lifecycle.LiveData
import com.jazbass.gaboum.GaboumApplication
import com.jazbass.gaboum.common.entities.GameEntity

class GameInteractor {

    fun getGameById(id: Long): LiveData<GameEntity> {
        return GaboumApplication.database.gaboumDao().getGameById(id)
    }

    fun getAllGames(): LiveData<MutableList<GameEntity>> {
        return GaboumApplication.database.gaboumDao().getAllGames()
    }

    suspend fun saveGame(gameEntity: GameEntity) {
        GaboumApplication.database.gaboumDao().addGame(gameEntity)
    }

    suspend fun updateGame(gameEntity: GameEntity) {
        GaboumApplication.database.gaboumDao().updateGame(gameEntity)
    }

}