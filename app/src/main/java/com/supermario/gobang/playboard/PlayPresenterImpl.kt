package com.supermario.gobang.playboard

import android.graphics.Color
import android.util.Log
import com.supermario.gobang.data.BaseData
import com.supermario.gobang.data.Chess
import javax.inject.Inject

/**
 * Created by lijia8 on 2016/10/21.
 */
class PlayPresenterImpl @Inject constructor(var view: PlayView, var dataModule: BaseData) : PlayPresenter {
    override fun viewCreated() {
        view.freezeToStop()
    }

    @Inject
    fun setViewListener() {
        view.setPresenter(this)
    }

    override fun subscribe() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unSubscribe() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startGame() {
        dataModule.reset()
        view.prepareToStart()
    }

    override fun stopGame() {
        view.freezeToStop()
    }

    override fun putChess(x: Int, y: Int, needsThink: Boolean) {
        val resultSuccess = dataModule.putChess(makeChess(x, y))
        Log.d("kkk", "result:" + resultSuccess)
        if (resultSuccess) {
            view.updateCurrentChess(dataModule.getCurrentChessState())
            if (needsThink) {
                askForNextStep()
            }
        }
    }

    fun askForNextStep() {
        dataModule.thinkForNextState { x, y ->
            putChess(x, y, false)
            view.updateScores(dataModule.getScoreB(), dataModule.getScoreW(), true)
        }
    }

    fun makeChess(x: Int, y: Int): Chess {

        val chess: Chess = Chess(Chess.COLOR.BLACK, x, y, 0)
        val stack = dataModule.getCurrentChessState()
        if (stack.size > 0) {
            val lastChess: Chess = stack.peek()
            chess.color = when (lastChess.color) {
                Chess.COLOR.WHITE -> Chess.COLOR.BLACK
                Chess.COLOR.BLACK -> Chess.COLOR.WHITE
                else -> Chess.COLOR.BLACK
            }
            chess.index = lastChess.index + 1
        }
        return chess
    }

    override fun rewind() {
        view.updateCurrentChess(dataModule.rewind())
    }
}