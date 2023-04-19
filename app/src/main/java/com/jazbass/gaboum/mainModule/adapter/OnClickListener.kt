package com.jazbass.gaboum.mainModule.adapter

import com.jazbass.gaboum.common.entities.GameEntity

interface OnClickListener {

    fun onClick(gameEntity: GameEntity)
    fun onDeleteGame(gameEntity: GameEntity)

}