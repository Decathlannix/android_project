package pt.ua.cm.n111763_114683_114715.androidproject.gameobjects

import android.graphics.Paint
import android.graphics.Rect

class Square( inColor : Int, inPosition : Pair<Int, Int>, val boardRef : Board) {

    companion object{
        enum class MOVEOPTIONS {
            CAN,
            CANT,
            STACK
        }
    }

    var paint = Paint()
    var position = Pair(0, 0)
    var dimensions = Rect(0, 0, 0, 0)
    var isStacked = false  // Can be useful in a future case where the board stores references instead of enums.

    init {
        paint.color = inColor
        position = inPosition
        setNewDimensions()
    }

    fun move(unitsToMove : Pair<Int, Int>){
        val firstVal = position.first + unitsToMove.first
        val secondVal = position.second + unitsToMove.second

        // Sanity check
        if(boardRef.checkIfPosIsOnBoard(firstVal, secondVal)){
            position = Pair(firstVal, secondVal)
            setNewDimensions()
        }
    }

    fun canMove(unitsToMove : Pair<Int, Int>) : MOVEOPTIONS {

        val adjacentState = boardRef.getIsAdjacentFree(position)
        var canMove = MOVEOPTIONS.CAN

        if (unitsToMove.first == -1 && adjacentState[0] == Board.Companion.TILESTATE.STACKED){

            canMove = MOVEOPTIONS.CANT
        }

        if (unitsToMove.first == 1 && adjacentState[1] == Board.Companion.TILESTATE.STACKED){
            canMove = MOVEOPTIONS.CANT
        }

        if (unitsToMove.second == 1 && adjacentState[2] == Board.Companion.TILESTATE.STACKED){
            canMove = MOVEOPTIONS.STACK
        }

        return canMove
    }

    fun setNewPosition(newPos : Pair<Int, Int>){

        // Sanity check
        if (boardRef.checkIfPosIsOnBoard(newPos.first, newPos.second)){
            position = Pair(newPos.first, newPos.second)
            setNewDimensions()
        }
    }

    fun checkNewPosition(newPos : Pair<Int, Int>) : MOVEOPTIONS {
        var state = MOVEOPTIONS.CAN
        if (boardRef.checkIfPosIsOccupied(newPos)){
            state = MOVEOPTIONS.CANT
        }
        return state
    }

    private fun setNewDimensions(){
        dimensions.left = Board.tileSize * position.first
        dimensions.top = Board.tileSize * position.second
        dimensions.right = Board.tileSize * position.first + Board.tileSize
        dimensions.bottom = Board.tileSize * position.second + Board.tileSize
    }
}