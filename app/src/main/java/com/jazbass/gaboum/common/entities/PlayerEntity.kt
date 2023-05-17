package com.jazbass.gaboum.common.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var gameId: Long,
    var name: String,
    var score: Int
) {constructor() : this(score = 0, name = "", gameId = 0)
}