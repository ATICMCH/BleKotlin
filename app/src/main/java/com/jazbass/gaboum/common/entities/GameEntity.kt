package com.jazbass.gaboum.common.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "GameEntity")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var gameId: Long
) {
    constructor() : this(gameId = 0)
}