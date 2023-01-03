package pt.ua.cm.n111763_114683_114715.androidproject.gameobjects

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GameView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs)  {

    private var isSet = false
    private lateinit var boardRef : Board

    private var bckgrndPaint = Paint()

    fun setup(inBoard : Board){
        boardRef = inBoard
        isSet = true
        bckgrndPaint.color = Color.GRAY
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.LTGRAY)
        canvas?.drawRect(0f, 0f, (Board.tileSize * Board.width).toFloat(), (Board.tileSize * Board.height).toFloat(), bckgrndPaint)

        if (isSet && !boardRef.gameOver){
            for (square in boardRef.currentTetromino.squares) {
                canvas?.drawRect(square.dimensions, square.paint)
            }
            for (square in boardRef.stackedSquares) {
                canvas?.drawRect(square.dimensions, square.paint)
            }

        }
    }
}