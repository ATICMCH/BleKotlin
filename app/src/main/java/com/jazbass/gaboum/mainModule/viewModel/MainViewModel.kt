package com.jazbass.gaboum.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.mainModule.model.MainInteractor

class MainViewModel: ViewModel() {

    private var gameList: MutableList<GameEntity> = mutableListOf()
    private var interactor: MainInteractor = MainInteractor()

    private val games: MutableLiveData<MutableList<GameEntity>> by lazy {
        MutableLiveData<MutableList<GameEntity>>().also {
            loadGames()
        }
    }

    private fun loadGames() {
        interactor.getAllGames()
    }

    fun getGames(): LiveData<MutableList<GameEntity>> = games

}