package com.supermario.gobang.data

import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import com.supermario.gobang.data.BaseData
import com.supermario.gobang.data.ai.AI
import java.util.*

/**
 * Created by lijia8 on 2016/10/19.
 */
class Data : BaseData {

    override fun judge(): Int {
        return ai.judge(positionArray)
    }

    override fun getScoreB(): Array<Array<IntArray>> {
        return positionPointArrayBlack
    }

    override fun getScoreW(): Array<Array<IntArray>> {
        return positionPointArrayWhite
    }

    val ai = AI()
    val positionArray = Array(15) { IntArray(15) }//int[15][15] x, y

    //int[15][15][5]: x, y, four direction, sum
    val positionPointArrayBlack = Array(15) { Array(15) { IntArray(5) } }

    //int[15][15][5]: x, y, four direction, sum
    val positionPointArrayWhite = Array(15) { Array(15) { IntArray(5) } }


    fun clear(array: Array<Array<IntArray>>) {
        for (i in 0..array.size - 1) {
            for (j in 0..array[i].size - 1) {
                for (h in 0..array[i][j].size - 1) {
                    array[i][j][h] = 0
                }
            }
        }
    }


    override fun reset() {
        chessStore.clear()
        for (i in 0..positionArray.size - 1) {
            for (j in 0..positionArray[i].size - 1) {
                positionArray[i][j] = 0
            }
        }
        clear(positionPointArrayBlack)
        clear(positionPointArrayWhite)
    }

    private var chessStore: Stack<Chess> = Stack()

    override fun putChess(chess: Chess): Boolean {
        val contains = chessStore.contains(chess)
        if (!contains) {
            chessStore.push(chess)
            positionArray[chess.x][chess.y] = chess.color
            for (i in 0..4) {
                positionPointArrayBlack[chess.x][chess.y][i] = 0
                positionPointArrayBlack[chess.x][chess.y][i] = 0
                positionPointArrayWhite[chess.x][chess.y][i] = 0
                positionPointArrayWhite[chess.x][chess.y][i] = 0
            }
        }
        return !contains

    }

    override fun rewind(): Stack<Chess> {
        val chess = chessStore.pop()
        positionArray[chess.x][chess.y] = 0
        return chessStore
    }

    override fun getCurrentChessState(): Stack<Chess> = chessStore

    override fun thinkForNextState(callback: (nextX: Int, nextY: Int) -> Unit) {
        ai.think(chessStore, positionArray,
                positionPointArrayBlack,
                positionPointArrayWhite,
                object : BaseData.Module2PresenterCallback {
                    override fun putNextChess(nextStepX: Int, nextStepY: Int) {
                        callback(nextStepX, nextStepY)
                    }
                })
    }

}