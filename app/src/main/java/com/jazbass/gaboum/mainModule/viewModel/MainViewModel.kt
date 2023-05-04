 package com.jazbass.gaboum.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

 class MainViewModel: ViewModel() {

    private var interactor: MainInteractor = MainInteractor()
    private var gameList: MutableList<GameEntity> = mutableListOf()

    private val games = interactor.games

    fun getGames(): LiveData<MutableList<GameEntity>> {return games}



    fun deleteGame(gameEntity: GameEntity) {
        executeAction {
            interactor.deleteGame(gameEntity)
        }
    }

    private fun executeAction(block: suspend () -> Unit): Job{
        return viewModelScope.launch {
            try{
                block()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }


}