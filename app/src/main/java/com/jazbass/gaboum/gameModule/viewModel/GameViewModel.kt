package com.jazbass.gaboum.gameModule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jazbass.gaboum.common.entities.GameEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val gameSelected = MutableLiveData<Long>()

    fun saveGame(gameEntity: GameEntity) {

    }

    fun executeAction(gameEntity: GameEntity, block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            block()
        }
    }

}