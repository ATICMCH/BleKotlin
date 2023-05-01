package com.jazbass.gaboum.gameModule.viewModel

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.gameModule.model.GameIterator

class GameViewModel : ViewModel() {

    private val gameSelected = MutableLiveData<Long>()
    private val interactor = GameIterator()
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