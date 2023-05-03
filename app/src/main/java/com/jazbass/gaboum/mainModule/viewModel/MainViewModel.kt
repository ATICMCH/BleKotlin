 package com.jazbass.gaboum.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.mainModule.model.MainInteractor

class MainViewModel: ViewModel() {

    private var interactor: MainInteractor = MainInteractor()
    private var gameList: MutableList<GameEntity> = mutableListOf()

    private val games = interactor.games

    fun getGames(): LiveData<MutableList<GameEntity>> {return games}

}