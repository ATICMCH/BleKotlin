package com.jazbass.gaboum.gameModule.model

import androidx.lifecycle.LiveData
import com.jazbass.gaboum.GameApplication
import com.jazbass.gaboum.common.database.GameWithPlayers
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.common.entities.PlayerEntity

class GameInteractor {

    fun getGameById(id: Long): LiveData<GameEntity> {
        return GameApplication.database.gameDao().getGameById(id)
    }

    fun getAllGames(): LiveData<MutableList<GameEntity>> {
        return GameApplication.database.gameDao().getAllGames()
    }

    suspend fun saveGame(gameEntity: GameEntity): Long {
        return GameApplication.database.gameDao().addGame(gameEntity)
    }

    suspend fun updateGame(gameEntity: GameEntity) {
        GameApplication.database.gameDao().updateGame(gameEntity)
    }


}