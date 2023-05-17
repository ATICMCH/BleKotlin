package com.jazbass.gaboum.common.database

import androidx.room.Embedded
import androidx.room.Relation
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.common.entities.PlayerEntity

data class GameWithPlayers(
    @Embedded
    val gameEntity: GameEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val players: List<PlayerEntity>
)