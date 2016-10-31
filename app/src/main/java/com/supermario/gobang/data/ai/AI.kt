package com.supermario.gobang.data.ai

import android.graphics.Color
import com.supermario.gobang.data.BaseData
import com.supermario.gobang.data.Chess
import com.supermario.gobang.data.Data
import java.util.*

/**
 * Created by lijia8 on 2016/10/21.
 */
class AI() {


    fun think(stack: Stack<Chess>, positionArray: Array<IntArray>,
              pointsBlack: Array<Array<IntArray>>,
              pointsWhite: Array<Array<IntArray>>,
              callback: BaseData.Module2PresenterCallback) {
        if (stack.size <= 0) {
            return
        }
        var maxPX = 0
        var maxPY = 0
        var maxScore = 0
        val thread = Thread({
            getFullPositionPoints(positionArray, pointsBlack, pointsWhite)
            when (stack.peek().color) {
                Chess.COLOR.BLACK -> {
                    for (i in 0..pointsWhite.size - 1) {
                        for (j in 0..pointsWhite[i].size - 1) {
                            if (pointsWhite[i][j][4] > maxScore && hasNeighbours(i, j, positionArray)) {
                                maxScore = pointsWhite[i][j][4]
                                maxPX = i
                                maxPY = j
                            }
                        }
                    }
                }
                Chess.COLOR.WHITE -> {
                    for (i in 0..pointsBlack.size - 1) {
                        for (j in 0..pointsBlack[i].size - 1) {
                            if (pointsBlack[i][j][4] > maxScore && hasNeighbours(i, j, positionArray)) {
                                maxScore = pointsBlack[i][j][4]
                                maxPX = i
                                maxPY = j
                            }
                        }
                    }
                }
            }
            callback.putNextChess(maxPX, maxPY)

        })
        thread.start()
    }

    //计算某点向左/右的棋型
    fun getPoints(positionArray: Array<IntArray>, x: Int, y: Int, color: Int, methodX: (Int) -> (Int), methodY: (Int) -> (Int)): Int {
        var count = 0
        var mX = methodX(x)
        var mY = methodY(y)
        while (mX >= 0 && mY >= 0 && mX < 15 && mY < 15) {

            if (positionArray[mX][mY] == color) {
                count += 1
            } else if (positionArray[mX][mY] == 0) {
                return count//open type
            } else {
                return -count//close type
            }

            mX = methodX(mX)
            mY = methodY(mY)
        }
        return if (count > 0) -count else Int.MIN_VALUE
    }

    /*
    * @leftCount: chess count at left
    * @rightCount: chess count at right
    * @typeLeft: left state, open(1)/close(-1)
    * @typeRight: typeRight state, open(1)/close(-1)
    *
    * */
    fun getThePositionPoints(leftCount: Int, rightCount: Int, typeLeft: Int, typeRight: Int): Int {
        var score = 0
        if (leftCount + rightCount >= 4) {
            score = 50000//next to win
        } else if (leftCount + rightCount == 3) {
            when (typeLeft + typeRight) {
                2 -> score = 8320 // both open
                0 -> score = 1000 // single open, single close
                -2 -> score = 0 // both close
            }
        } else if (leftCount + rightCount == 2) {
            when (typeLeft + typeRight) {
                2 -> score = 1500 // both open
                0 -> score = 200 // single open, single close
                -2 -> score = 0 // both close
            }
        } else if (leftCount + rightCount == 1) {
            when (typeLeft + typeRight) {
                2 -> score = 250 // both open
                0 -> score = 40 // single open, single close
                -2 -> score = 0 // both close
            }
        } else {
            when (typeLeft + typeRight) {
                2 -> score = 50 // both open
                0 -> score = 10 // single open, single close
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
    fun calculateDirectionPoints(positionArray: Array<IntArray>,
                                 x: Int, y: Int, color: Int,
                                 methodLeftX: (Int) -> Int, methodLeftY: (Int) -> Int,
                                 methodRightX: (Int) -> Int, methodRightY: (Int) -> Int): Int {
        var leftCount = 0
        var rightCount = 0
        var typeLeft = 1
        var typeRight = 1
        //-
        val leftSize = getPoints(positionArray, x, y, color, methodLeftX, methodLeftY)
        if (leftSize >= 0) {
            leftCount += leftSize
        } else {
            if (leftSize == Int.MIN_VALUE) {

            } else {
                leftCount += Math.abs(leftCount)
            }
            typeLeft = -1
        }

        //+
        val rightSize = getPoints(positionArray, x, y, color, methodRightX, methodRightY)
        if (rightSize >= 0) {
            rightCount += rightSize
        } else {
            if (rightSize == Int.MIN_VALUE) {

            } else {
                rightCount += Math.abs(rightCount)
            }
            typeRight = -1
        }
        val score = getThePositionPoints(leftCount, rightCount, typeLeft, typeRight)

        return score
    }

    fun getFullPositionPoints(positionArray: Array<IntArray>, pointsBlack: Array<Array<IntArray>>, pointsWhite: Array<Array<IntArray>>) {
        //1. Find each empty point
        //2. On each empty point, call calculateDirectionPoints in each direction
        val methodStay: (Int) -> Int = { it -> it }
        val methodDecrease: (Int) -> Int = { it -> it - 1 }
        val methodIncrease: (Int) -> Int = { it -> it + 1 }
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

    }

    //find if a point has neighbours in its radius
    fun hasNeighbours(x: Int, y: Int, array: Array<IntArray>): Boolean {
        val tempArray = array.clone()
        val neighboursRadius = 2

        if (x < 0 || y < 0 || x >= 15 || y >= 15) return false

        val startX = x - neighboursRadius
        val startY = y - neighboursRadius

        for (i in (startX..startX + neighboursRadius * 2)) {
            if (i >= 0 && i < 15) {
                for (j in (startY..startY + neighboursRadius * 2)) {
                    if (j >= 0 && j < 15) {
                        if (tempArray[i][j] != 0) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }


}