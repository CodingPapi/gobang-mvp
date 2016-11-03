package com.supermario.gobang.data.ai

import android.graphics.Color
import android.util.Log
import com.supermario.gobang.data.BaseData
import com.supermario.gobang.data.Chess
import com.supermario.gobang.data.Data
import java.util.*

/**
 * Created by lijia8 on 2016/10/21.
 */
class AI() {

    private data class CountState(val count: Int, val type: Int)
    private data class AvailablePoint(val x: Int, val y: Int)

    fun think(stack: Stack<Chess>, positionArray: Array<IntArray>,
              pointsBlack: Array<Array<IntArray>>,
              pointsWhite: Array<Array<IntArray>>,
              callback: BaseData.Module2PresenterCallback) {
        val currentStets = stack.size
        if (currentStets <= 0) {
            return
        }
        val thread = Thread({
            cut = 0
            sum = 0
            val (x, y) = maxMinSearch(positionArray, 4, stack.peek().color)
            callback.putNextChess(x, y)
            Log.d("kkk", "cut:$cut,sum:$sum")
        })
        thread.start()
    }

    //Global Score = Global Computer Score - Global Human Score
    //So, bigger Global Score means good to computer and smaller score means good to human
    private fun getCurrentGlobalScore(computerScoreArray: Array<Array<IntArray>>, humanScoreArray: Array<Array<IntArray>>): Int {
        var sumComputer = 0
        var sumHuman = 0
        for (i in computerScoreArray) {
            for (j in i) {
                sumComputer += j[4]
            }
        }
        for (i in humanScoreArray) {
            for (j in i) {
                sumHuman += j[4]
            }
        }
        val global = sumComputer - sumHuman
        return global
    }

    //Generate all available Points
    private fun inspireGeneratePoints(positionArray: Array<IntArray>): ArrayList<AvailablePoint> {

        val workList: ArrayList<AvailablePoint> = ArrayList()

        for (i in 0..positionArray.size - 1) {
            for (j in 0..positionArray[i].size - 1) {
                if (positionArray[i][j] == 0 && hasNeighbours(i, j, positionArray, 2)) {
                    workList.add(AvailablePoint(i, j))
                }
            }
        }

        return workList
    }

    private fun getNextChessColor(lastChessColor: Int): Int {
        var color = Chess.COLOR.BLACK
        when (lastChessColor) {
            Chess.COLOR.BLACK -> color = Chess.COLOR.WHITE
            Chess.COLOR.WHITE -> color = Chess.COLOR.BLACK
        }
        return color
    }

    val MAX = Int.MAX_VALUE
    val MIN = Int.MIN_VALUE
    var sum = 0
    var cut = 0
    //max min search
    private fun maxMinSearch(positionArray: Array<IntArray>, deep: Int, lastChess: Int): AvailablePoint {
        var best = MIN
        val availablePoints = inspireGeneratePoints(positionArray)
        val bestPoints: ArrayList<AvailablePoint> = ArrayList()
        val chessColor = getNextChessColor(lastChess)

        for (point in availablePoints) {
            //try to put a chess, we assume that next put the human will choose a minimum one as his best place

            positionArray[point.x][point.y] = chessColor
            // human choose a best score for himself, which him want is the min, return its global score
            val v = min(positionArray, deep - 1, chessColor, if (best > MIN) best else MIN, MAX)
            //AI want this score is the max in all possibilities that human can choose
            if (v > best) {
                bestPoints.clear()
                best = v
                bestPoints.add(point)
            } else if (v == best) {
                bestPoints.add(point)
            }
            positionArray[point.x][point.y] = 0//remember clear our put
        }
        val result = bestPoints[Random().nextInt(bestPoints.size)]//random choose one in best puts
        return result
    }

    //in min, we will try to choose a best put for human, and return its global score
    private fun min(positionArray: Array<IntArray>, deep: Int, lastChess: Int,
            alpha: Int, beta: Int): Int {
        sum++
        val v = evaluateGlobalScore(positionArray, lastChess)
        if (deep <= 0 || judge(positionArray) != 0) {
            return v
        }

        var best = MAX
        val availablePoints = inspireGeneratePoints(positionArray)
        val chessColor = getNextChessColor(lastChess)
        for ((x, y) in availablePoints) {
            positionArray[x][y] = chessColor
            // AI choose a best score for himself, which him want is the max, return its global score
            val v = max(positionArray, deep - 1, chessColor, if (best < alpha) best else alpha, beta)
            positionArray[x][y] = 0//remember clear our put
            //human want this score is the min in all possibilities that AI can choose
            if (v < best) {
                best = v
            }

            if (v < beta) {// beta cut
                cut++
                return best
            }
        }
        return best
    }

    //in max, we will try to choose a best put for AI, and return its global score
    private fun max(positionArray: Array<IntArray>, deep: Int, lastChess: Int,
            alpha: Int, beta: Int): Int {
        sum++
        val v = evaluateGlobalScore(positionArray, lastChess)
        if (deep <= 0 || judge(positionArray) != 0) {
            return v
        }

        var best = MAX
        val availablePoints = inspireGeneratePoints(positionArray)
        val chessColor = getNextChessColor(lastChess)
        for ((x, y) in availablePoints) {
            positionArray[x][y] = chessColor
            // human choose a best score for himself, which him want is the min, return its global score
            val v = min(positionArray, deep - 1, chessColor, alpha, if (best > beta) best else beta)
            positionArray[x][y] = 0//remember clear our put
            //AI want this score is the max in all possibilities that human can choose
            if (v > best) {
                best = v
            }
            if (v > alpha) {
                cut++
                return best
            }
        }
        return best
    }

    fun attack() {


    }

    fun defence() {

    }

    // return winner color or return 0
    fun judge(positionArray: Array<IntArray>): Int {
        //1. Find each NOT empty point
        //2. On each point, call judgeSingleDirection in each direction
        val methodStay: (Int) -> Int = { it -> it }
        val methodDecrease: (Int) -> Int = { it -> it - 1 }
        val methodIncrease: (Int) -> Int = { it -> it + 1 }
        for (i in 0..positionArray.size - 1) {
            for (j in 0..positionArray[i].size - 1) {
                if (positionArray[i][j] == Chess.COLOR.BLACK || positionArray[i][j] == Chess.COLOR.WHITE) {
                    // -
                    val result1 = judgeSingleDirection(positionArray, i, j,
                            methodLeftX = methodDecrease, methodLeftY = methodStay,
                            methodRightX = methodIncrease, methodRightY = methodStay)
                    // |
                    val result2 = judgeSingleDirection(positionArray, i, j,
                            methodLeftX = methodStay, methodLeftY = methodDecrease,
                            methodRightX = methodStay, methodRightY = methodIncrease)
                    // /
                    val result3 = judgeSingleDirection(positionArray, i, j,
                            methodLeftX = methodIncrease, methodLeftY = methodDecrease,
                            methodRightX = methodDecrease, methodRightY = methodIncrease)
                    // \
                    val result4 = judgeSingleDirection(positionArray, i, j,
                            methodLeftX = methodDecrease, methodLeftY = methodDecrease,
                            methodRightX = methodIncrease, methodRightY = methodIncrease)
                    if (result1 || result2 || result3 || result4) {
                        return positionArray[i][j]
                    }
                }
            }
        }
        return 0

    }

    private fun judgeSingleDirection(positionArray: Array<IntArray>, x: Int, y: Int,
                             methodLeftX: (Int) -> Int, methodLeftY: (Int) -> Int,
                             methodRightX: (Int) -> Int, methodRightY: (Int) -> Int): Boolean {

        //-
        var leftCount = getPoints(positionArray, x, y, positionArray[x][y], methodLeftX, methodLeftY)
        //+
        var rightCount = getPoints(positionArray, x, y, positionArray[x][y], methodRightX, methodRightY)

        leftCount = if (leftCount >= SHIFT) leftCount - SHIFT else leftCount
        rightCount = if (rightCount >= SHIFT) rightCount - SHIFT else rightCount
        val result = leftCount + rightCount

        return result >= 4


    }

    private val SHIFT = 8
    //计算某点向左/右的棋型
    private fun getPoints(positionArray: Array<IntArray>, x: Int, y: Int, color: Int, methodX: (Int) -> (Int), methodY: (Int) -> (Int)): Int {
        var count = 0
        var mX = methodX(x)
        var mY = methodY(y)
        while (mX >= 0 && mY >= 0 && mX < 15 && mY < 15) {
            if (positionArray[mX][mY] == 0) {
                return count
            } else if (positionArray[mX][mY] == color) {
                count += 1
            } else {
                return SHIFT + count
            }

            mX = methodX(mX)
            mY = methodY(mY)
        }

        return SHIFT + count
    }

    /*
    * @leftCount: chess count at left
    * @rightCount: chess count at right
    * @typeLeft: left state, open(1)/close(-1)
    * @typeRight: typeRight state, open(1)/close(-1)
    *
    * */
    private fun getThePositionPoints(lC: Int, rC: Int): Int {
        var score = 0
        var typeLeft = 1
        var typeRight = 1
        var leftCount = 0
        var rightCount = 0
        if (lC >= SHIFT) {
            typeLeft = -1
        }

        if (rC >= SHIFT) {
            typeRight = -1
        }

        leftCount = lC and SHIFT xor SHIFT
        rightCount = rC and SHIFT xor SHIFT


        if (leftCount + rightCount >= 4) {
            score = 500000//next to win
        } else if (leftCount + rightCount == 3) {
            when (typeLeft + typeRight) {
                2 -> score = 50000 // both open
                0 -> score = 5000 // single open, single close
                -2 -> score = 0 // both close
            }
        } else if (leftCount + rightCount == 2) {
            when (typeLeft + typeRight) {
                2 -> score = 5000 // both open
                0 -> score = 500 // single open, single close
                -2 -> score = 0 // both close
            }
        } else if (leftCount + rightCount == 1) {
            when (typeLeft + typeRight) {
                2 -> score = 500 // both open
                0 -> score = 50 // single open, single close
                -2 -> score = 0 // both close
            }
        } else {
            when (typeLeft + typeRight) {
                2 -> score = 50 // both open
                0 -> score = 5 // single open, single close
                -2 -> score = 0 // both close
            }
        }
        return score
    }

    //direction: - | / \ -> 0 1 2 3
    // -
    /*
    * @positionArray: chess state array
    * @x: x coordinate
    * @y: y coordinate
    * @methodX: method to change x
    * @methodY: method to change y
    *
    * */
    private fun calculateDirectionPoints(positionArray: Array<IntArray>,
                                 x: Int, y: Int, color: Int,
                                 methodLeftX: (Int) -> Int, methodLeftY: (Int) -> Int,
                                 methodRightX: (Int) -> Int, methodRightY: (Int) -> Int): Int {
        //-
        val leftCount = getPoints(positionArray, x, y, color, methodLeftX, methodLeftY)
        //+
        val rightCount = getPoints(positionArray, x, y, color, methodRightX, methodRightY)

        val score = getThePositionPoints(leftCount, rightCount)

        return score
    }

    private fun evaluateGlobalScore(positionArray: Array<IntArray>, lastChess: Int): Int {
        //1. Find each empty point
        //2. On each empty point, call calculateDirectionPoints in each direction
        //3. call getCurrentGlobalScore to get a global score
        val methodStay: (Int) -> Int = { it -> it }
        val methodDecrease: (Int) -> Int = { it -> it - 1 }
        val methodIncrease: (Int) -> Int = { it -> it + 1 }

        val pointsBlack: Array<Array<IntArray>> = Array(15) { Array(15) { IntArray(5) } }
        val pointsWhite: Array<Array<IntArray>> = Array(15) { Array(15) { IntArray(5) } }

        for (i in 0..positionArray.size - 1) {
            for (j in 0..positionArray[i].size - 1) {
                if (positionArray[i][j] == 0) {
                    // -
                    val score1B = calculateDirectionPoints(positionArray, i, j, Chess.COLOR.BLACK,
                            methodLeftX = methodDecrease, methodLeftY = methodStay,
                            methodRightX = methodIncrease, methodRightY = methodStay)
                    pointsBlack[i][j][0] = score1B
                    val score1W = calculateDirectionPoints(positionArray, i, j, Chess.COLOR.WHITE,
                            methodLeftX = methodDecrease, methodLeftY = methodStay,
                            methodRightX = methodIncrease, methodRightY = methodStay)
                    pointsWhite[i][j][0] = score1W
                    // |
                    val score2B = calculateDirectionPoints(positionArray, i, j, Chess.COLOR.BLACK,
                            methodLeftX = methodStay, methodLeftY = methodDecrease,
                            methodRightX = methodStay, methodRightY = methodIncrease)
                    pointsBlack[i][j][1] = score2B
                    val score2W = calculateDirectionPoints(positionArray, i, j, Chess.COLOR.WHITE,
                            methodLeftX = methodStay, methodLeftY = methodDecrease,
                            methodRightX = methodStay, methodRightY = methodIncrease)
                    pointsWhite[i][j][1] = score2W
                    // /
                    val score3B = calculateDirectionPoints(positionArray, i, j, Chess.COLOR.BLACK,
                            methodLeftX = methodIncrease, methodLeftY = methodDecrease,
                            methodRightX = methodDecrease, methodRightY = methodIncrease)
                    pointsBlack[i][j][2] = score3B
                    val score3W = calculateDirectionPoints(positionArray, i, j, Chess.COLOR.WHITE,
                            methodLeftX = methodIncrease, methodLeftY = methodDecrease,
                            methodRightX = methodDecrease, methodRightY = methodIncrease)
                    pointsWhite[i][j][2] = score3W
                    // \
                    val score4B = calculateDirectionPoints(positionArray, i, j, Chess.COLOR.BLACK,
                            methodLeftX = methodDecrease, methodLeftY = methodDecrease,
                            methodRightX = methodIncrease, methodRightY = methodIncrease)
                    pointsBlack[i][j][3] = score4B
                    val score4W = calculateDirectionPoints(positionArray, i, j, Chess.COLOR.WHITE,
                            methodLeftX = methodDecrease, methodLeftY = methodDecrease,
                            methodRightX = methodIncrease, methodRightY = methodIncrease)
                    pointsWhite[i][j][3] = score4W

                    pointsBlack[i][j][4] = score1B + score2B + score3B + score4B
                    pointsWhite[i][j][4] = score1W + score2W + score3W + score4W
                }
            }
        }

//        var point = 0
//        when(lastChess) {
//            Chess.COLOR.BLACK -> getCurrentGlobalScore(pointsWhite, pointsBlack)
//            Chess.COLOR.WHITE -> getCurrentGlobalScore(pointsBlack, pointsWhite)
//        }
        val point = getCurrentGlobalScore(pointsWhite, pointsBlack)

        return point

    }

    //find if a point has neighbours in its radius
    private fun hasNeighbours(x: Int, y: Int, array: Array<IntArray>, distance: Int): Boolean {
        val tempArray = array.clone()

        if (x < 0 || y < 0 || x >= 15 || y >= 15) return false

        val startX = x - distance
        val startY = y - distance

        for (i in (startX..startX + distance * 2)) {
            if (i >= 0 && i < 15) {
                for (j in (startY..startY + distance * 2)) {
                    if (j >= 0 && j < 15) {
                        if (tempArray[i][j] == Chess.COLOR.BLACK ||
                                tempArray[i][j] == Chess.COLOR.WHITE) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }


}