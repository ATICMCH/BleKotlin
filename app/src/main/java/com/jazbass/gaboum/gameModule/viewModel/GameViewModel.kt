package com.jazbass.gaboum.gameModule.viewModel

import android.util.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.common.entities.PlayerEntity
import com.jazbass.gaboum.common.utils.Constants
import com.jazbass.gaboum.gameModule.model.GameInteractor

const val TAG = "View Model"
class GameViewModel : ViewModel() {

    private val result = MutableLiveData<Any>()
    //private val gameSelected = MutableLiveData<Long>()
    private val interactor: GameInteractor = GameInteractor()
    private val showProgress: MutableLiveData<Boolean> = MutableLiveData()
    private val gameSelected = MutableLiveData<GameEntity>()

    private var gameId = 0L

//    fun setGameSelected(gameEntity: GameEntity){
//        gameId = gameEntity.id
//    }

    fun setGameSelected(gameEntity: GameEntity){
        gameSelected.value = gameEntity
    }

    fun getGameSelected(): LiveData<GameEntity>{
        //return interactor.getGameById(gameId)
        return gameSelected
    }

//    fun getGamePlayers(): LiveData<List<PlayerEntity>>{
//        return interactor.getGamePlayers(gameSelected.value!!)
//    }

    fun isShowProgress(): LiveData<Boolean>{
        return showProgress
    }

    fun saveGame(gameEntity: GameEntity) {
        executeAction(gameEntity){
            interactor.saveGame(gameEntity)
        }
    }

    fun setResult(value: Any){
        result.value = value
    }

    fun getResult() = result

    private fun executeAction(gameEntity: GameEntity, block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            showProgress.value = Constants.SHOW
            try {
                block()
                result.value = gameEntity
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                showProgress.value = Constants.HIDE
            }
        }
    }

}