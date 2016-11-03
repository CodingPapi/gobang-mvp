package com.supermario.gobang.playboard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.supermario.gobang.data.Chess
import java.util.*

/**
 * Created by lijia8 on 2016/10/20.
 */
class ChessBoard : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val numOfLines = 15
    var boardWidth = 0
    var gapBetweenLines = 0
    var linePaint = Paint()
    var blackDotPaint = Paint()
    var chessPaint = Paint()
    var chessRectPaint = Paint()

    var scorePaintB = Paint()
    var scorePaintW = Paint()
    var scorePaintMax = Paint()

    var edgeMargin = 0

    var chessStore: Stack<Chess> = Stack()

    var scoreB: Array<Array<IntArray>>? = null
    var scoreW: Array<Array<IntArray>>? = null
    var showScore = false

    //used for test
    fun updateScore(arrayB: Array<Array<IntArray>>, arrayW: Array<Array<IntArray>>, show: Boolean) {
        scoreB = arrayB
        scoreW = arrayW
        showScore = show
    }

    fun drawScore(canvas: Canvas?, array: Array<Array<IntArray>>?,
                  gap: Int, edge: Int,
                  paint: Paint,
                  black: Boolean) {

        if (!showScore) return

        val temp = if (black) 0 else 10

        var max = 0
        if (array != null) {
            for (i in 0..array.size - 1) {
                for (j in 0..array[i].size - 1) {
                    if (array[i][j][4] > max) {
                        max = array[i][j][4]
                        canvas?.drawText(array[i][j][4].toString(), i * gap + edge / 2.toFloat(), j * gap + edge / 2 + temp.toFloat(), scorePaintMax)
                    } else {
                        canvas?.drawText(array[i][j][4].toString(), i * gap + edge / 2.toFloat(), j * gap + edge / 2 + temp.toFloat(), paint)
                    }
                }
            }
        }
    }

    fun updateChessStore(store: Stack<Chess>) {
        handler.post {
            chessStore = store
            invalidate()
        }
    }

    fun resetData() {
        chessStore.clear()
        invalidate()
    }


    internal var touchCallback: TouchCallback? = null

    interface TouchCallback {
        fun askToPutChessAt(x: Int, y: Int)
    }

    init {
        linePaint.color = Color.BLACK
        blackDotPaint.color = Color.BLACK
        blackDotPaint.strokeWidth = 5f
        chessRectPaint.color = Color.GREEN
        chessRectPaint.style = Paint.Style.STROKE
        chessRectPaint.strokeWidth = 2f

        scorePaintB.color = Color.BLACK
        scorePaintB.strokeWidth = 0.5f
        scorePaintW.color = Color.RED
        scorePaintW.strokeWidth = 0.5f
        scorePaintMax.color = Color.GREEN
        scorePaintMax.strokeWidth = 0.5f
    }

    override fun onDraw(canvas: Canvas?) {
        drawLines(canvas, gapBetweenLines.toFloat(), numOfLines, edgeMargin)
        drawChess(canvas, gapBetweenLines, edgeMargin, chessStore)
        drawLastChessRect(canvas, gap = gapBetweenLines, edge = edgeMargin, rectWidth = gapBetweenLines / 2)
        drawScore(canvas, scoreB, gapBetweenLines, edgeMargin, scorePaintB, true)
        drawScore(canvas, scoreW, gapBetweenLines, edgeMargin, scorePaintW, false)
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        var touchPoint = Point()
        var pX = 0
        var pY = 0
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val dividerX = event.x - edgeMargin
                val dividerY = event.y - edgeMargin
                val remainderX = dividerX % gapBetweenLines
                val remainderY = dividerY % gapBetweenLines
                pX = (dividerX / gapBetweenLines).toInt()
                pY = (dividerY / gapBetweenLines).toInt()

                pX = if (remainderX > gapBetweenLines / 2 && pX + 1 < numOfLines) pX + 1 else pX
                pY = if (remainderY > gapBetweenLines / 2 && pY + 1 < numOfLines) pY + 1 else pY

                touchCallback?.askToPutChessAt(pX, pY)
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        boardWidth = Math.min(measuredHeight, measuredWidth)
        gapBetweenLines = boardWidth / numOfLines
        edgeMargin = gapBetweenLines / 2
        setMeasuredDimension(boardWidth, boardWidth)
    }

    fun drawLines(canvas: Canvas?, gap: Float, lineNum: Int, edge: Int) {
        val width = (lineNum - 1) * gap
        for (i in 0..(lineNum - 1)) {
            canvas?.drawLine(i * gap + edge, 0f + edge, i * gap + edge, width + edge, linePaint)
        }
        for (i in 0..(lineNum - 1)) {
            canvas?.drawLine(0f + edge, i * gap + edge, width + edge, i * gap + edge, linePaint)
        }
        //Draw black dots
        canvas?.drawPoints(floatArrayOf(gap * 3 + edge, gap * 3 + edge,
                gap * 11 + edge, gap * 3 + edge, gap * 7 + edge, gap * 7 + edge,
                gap * 3 + edge, gap * 11 + edge, gap * 11 + edge, gap * 11 + edge), blackDotPaint)
    }

    fun drawChess(canvas: Canvas?, gap: Int, edge: Int, stack: Stack<Chess>) {
        chessPaint.strokeWidth = gap / 2.toFloat()
        val tempStack = stack.clone() as Stack<Chess>
        for ((color, x, y) in tempStack) {
            chessPaint.color = color
            canvas?.drawPoint(x * gap.toFloat() + edge, y * gap.toFloat() + edge, chessPaint)
        }
    }

    fun drawLastChessRect(canvas: Canvas?, gap: Int, edge: Int, rectWidth: Int) {
        if (chessStore.size > 0) {
            val lastChess = chessStore.peek()
            val position: PointF = PointF(lastChess.x * gap + edge - rectWidth / 2.toFloat(), lastChess.y * gap + edge - rectWidth / 2.toFloat())
            val path: Path = Path()
            path.addRect(position.x, position.y, position.x + rectWidth, position.y + rectWidth, Path.Direction.CCW)
            canvas?.drawPath(path, chessRectPaint)
        } else {
            return
        }
    }

}