package com.jazbass.gaboum.common.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "PlayerEntity")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "game_id") var gameId: Long?,
    var name: String?,
    var score: Int?
) {
    constructor() : this(gameId = 0L, score = 0, name = "")
}