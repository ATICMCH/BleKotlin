package com.jazbass.gaboum.common.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "GameEntity", indices = [Index(value = ["player1"], unique = false)])
data class GameEntity(@PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        var player1: String,
        var player2: String,
        var scorePlayer1: Int,
        var scorePlayer2: Int ) {
    constructor() : this(player1 = "", player2 = "", scorePlayer1 = 0, scorePlayer2 = 0)
}