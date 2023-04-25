package com.jazbass.gaboum.mainModule.model

import com.jazbass.gaboum.GameApplication
import com.jazbass.gaboum.common.entities.GameEntity

class MainInteractor {

    suspend fun deleteGame(gameEntity: GameEntity) {
        GameApplication.database.gameDao().deleteGame(gameEntity)
    }

     fun getAllGames() = GameApplication.database.gameDao().getAllGames()

}