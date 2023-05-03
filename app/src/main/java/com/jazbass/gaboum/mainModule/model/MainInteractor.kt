package com.jazbass.gaboum.mainModule.model

import androidx.lifecycle.map
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.jazbass.gaboum.GameApplication
import com.jazbass.gaboum.common.entities.GameEntity

class MainInteractor {

    suspend fun deleteGame(gameEntity: GameEntity) {
        GameApplication.database.gameDao().deleteGame(gameEntity)
    }

    val games: LiveData<MutableList<GameEntity>> = liveData{
        val gamesLiveData = GameApplication.database.gameDao().getAllGames()
        emitSource(gamesLiveData.map { games ->
            games.sortedBy { it.id }.toMutableList()
        })
    }

}