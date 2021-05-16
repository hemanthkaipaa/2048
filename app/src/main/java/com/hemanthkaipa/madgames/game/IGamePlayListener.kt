package com.hemanthkaipa.madgames.game

interface IGamePlayListener {
    fun updateTileValue(action : GameMoveRecord)
    fun wonGame()
    fun lostGame()
    fun UpdateScore(score: Int)
}