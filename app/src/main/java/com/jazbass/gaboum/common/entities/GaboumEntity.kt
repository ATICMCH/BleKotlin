package com.jazbass.gaboum.common.entities

import androidx.room.PrimaryKey

data class GaboumEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var player1: String,
    var player2: String,
    var scorePlayer1: Int,
    var scorePlayer2: Int) {
    constructor() : this(player1 = "", player2 = "", scorePlayer1 = 0, scorePlayer2 = 0)
}