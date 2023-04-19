package com.jazbass.gaboum.gameModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.gameModule.model.GameInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val gameSelected = MutableLiveData<Long>()
    private val interactor = GameInteractor()
    private val result = MutableLiveData<Any>()

    fun setGameSelected(id: Long){
        gameSelected.value =id
    }

    fun getGameSelected(): LiveData<GameEntity>{
        return interactor.getGameById(gameSelected.value!!)
    }

    fun saveGame(gameEntity: GameEntity) {
        executeAction(gameEntity){
            interactor.saveGame(gameEntity)
        }
    }

    private fun executeAction(gameEntity: GameEntity, block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            block()
            result.value = gameEntity
        }
    }

}