package com.jazbass.gaboum.gameModule.model

import androidx.lifecycle.LiveData
import com.jazbass.gaboum.GameApplication
import com.jazbass.gaboum.common.entities.GameEntity

class GameInteractor {

    fun getGameById(id: Long): LiveData<GameEntity> {
        return GameApplication.database.gaboumDao().getGameById(id)
    }

    fun getAllGames(): LiveData<MutableList<GameEntity>> {
        return GameApplication.database.gaboumDao().getAllGames()
    }

    suspend fun saveGame(gameEntity: GameEntity) {
        GameApplication.database.gaboumDao().addGame(gameEntity)
    }

    suspend fun updateGame(gameEntity: GameEntity) {
        GameApplication.database.gaboumDao().updateGame(gameEntity)
    }

}