package com.supermario.gobang.data

import java.util.*

/**
 * Created by lijia8 on 2016/10/21.
 */
interface BaseData {

    fun reset()

    fun putChess(chess: Chess): Boolean

    fun rewind(): Stack<Chess>

    interface Module2PresenterCallback {
        fun putNextChess(nextStepX: Int, nextStepY: Int)
    }

    fun thinkForNextState(callback: (nextX: Int, nextY: Int) -> Unit)

    fun getCurrentChessState() : Stack<Chess>

    fun getScoreB(): Array<Array<IntArray>>
    fun getScoreW(): Array<Array<IntArray>>
}