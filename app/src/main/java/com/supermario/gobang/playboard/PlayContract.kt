package com.supermario.gobang.playboard

import com.supermario.gobang.BasePresenter
import com.supermario.gobang.BaseView
import com.supermario.gobang.data.Chess
import java.util.*

/**
 * Created by lijia8 on 2016/10/21.
 */

interface PlayPresenter : BasePresenter {
    fun viewCreated()
    fun startGame()
    fun stopGame()
    fun putChess(x: Int, y: Int, needsThinkNextStep: Boolean)
    fun rewind()
}

interface PlayView : BaseView {
    fun updateScores(scoreArrayB: Array<Array<IntArray>>, scoreArrayW: Array<Array<IntArray>>, show: Boolean)
    fun updateCurrentChess(chessState: Stack<Chess>)
    fun showGameResult(winner: Int)
    fun prepareToStart()
    fun freezeToStop()
    fun setPresenter(presenter: PlayPresenter)
}

