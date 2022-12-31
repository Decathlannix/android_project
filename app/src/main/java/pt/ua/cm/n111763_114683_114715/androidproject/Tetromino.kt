package pt.ua.cm.n111763_114683_114715.androidproject

import android.graphics.Color
import android.util.Log
import kotlin.math.ceil

class Tetromino(inPosition : Pair<Int, Int>, val type : String, val boardRef : Board) {

    companion object{
        val types = arrayOf("I", "O", "T", "J", "L", "S", "Z" )

        // Default position of 4 squares on the board in order to make the intended tetromino.
        // First square is the "top-leftest" and last is the "bottom-rightest".
        // All squares in a row are set sequentially, before moving to the row below.
        val dimensionsForTypes = mapOf(
            types[0] to arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0)),
            types[1] to arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1)),
            types[2] to arrayOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1)),
            types[3] to arrayOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1)),
            types[4] to arrayOf(Pair(2, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1)),
            types[5] to arrayOf(Pair(1, 0), Pair(2, 0), Pair(0, 1), Pair(1, 1)),
            types[6] to arrayOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(2, 1))
        )

        val colorsForTypes = mapOf(
            types[0] to Color.CYAN,
            types[1] to Color.YELLOW,
            types[2] to -8388480,  // PURPLE
            types[3] to Color.BLUE,
            types[4] to -23296,  // ORANGE
            types[5] to Color.GREEN,
            types[6] to Color.RED
        )

        // Offset to be applied to each square of a tetromino in order to make them rotate 90 degrees.
        // For each type, there are 4 rotation states, and for each state, an offset must be applied to all 4 squares.
        val rotationsForTypes = mapOf(
            types[0] to arrayOf(arrayOf(Pair(1, -1), Pair(0, 0), Pair(-1, 1), Pair(-2, 2)),
                                arrayOf(Pair(-1, 1), Pair(0, 0), Pair(1, -1), Pair(2, -2)),
                                arrayOf(Pair(1, -1), Pair(0, 0), Pair(-1, 1), Pair(-2, 2)),
                                arrayOf(Pair(-1, 1), Pair(0, 0), Pair(1, -1), Pair(2, -2))),

            types[1] to arrayOf(arrayOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0)),
                                arrayOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0)),
                                arrayOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0)),
                                arrayOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0))),

            types[2] to arrayOf(arrayOf(Pair(1, 1), Pair(1, -1), Pair(0, 0), Pair(-1, 1)),
                                arrayOf(Pair(-1, 1), Pair(1, 1), Pair(0, 0), Pair(-1, -1)),
                                arrayOf(Pair(-1, -1), Pair(-1, 1), Pair(0, 0), Pair(1, -1)),
                                arrayOf(Pair(1, -1), Pair(-1, -1), Pair(0, 0), Pair(1, 1))),

            types[3] to arrayOf(arrayOf(Pair(2, 0), Pair(1, -1), Pair(0, 0), Pair(-1, 1)),
                                arrayOf(Pair(0, 2), Pair(1, 1), Pair(0, 0), Pair(-1, -1)),
                                arrayOf(Pair(-2, 0), Pair(-1, 1), Pair(0, 0), Pair(1, -1)),
                                arrayOf(Pair(0, -2), Pair(-1, -1), Pair(0, 0), Pair(1, 1))),


            types[4] to arrayOf(arrayOf(Pair(0, 2), Pair(1, -1), Pair(0, 0), Pair(-1, 1)),
                                arrayOf(Pair(-2, 0), Pair(1, 1), Pair(0, 0), Pair(-1, -1)),
                                arrayOf(Pair(0, -2), Pair(-1, 1), Pair(0, 0), Pair(1, -1)),
                                arrayOf(Pair(2, 0), Pair(-1, -1), Pair(0, 0), Pair(1, 1))),


            types[5] to arrayOf(arrayOf(Pair(1, 1), Pair(0, 2), Pair(1, -1), Pair(0, 0)),
                                arrayOf(Pair(-1, 1), Pair(-2, 0), Pair(1, 1), Pair(0, 0)),
                                arrayOf(Pair(-1, -1), Pair(0, -2), Pair(-1, 1), Pair(0, 0)),
                                arrayOf(Pair(1, -1), Pair(2, 0), Pair(-1, -1), Pair(0, 0))),


            types[6] to arrayOf(arrayOf(Pair(2, 0), Pair(1, 1), Pair(0, 0), Pair(-1, 1)),
                                arrayOf(Pair(0, 2), Pair(-1, 1), Pair(0, 0), Pair(-1, -1)),
                                arrayOf(Pair(-2, 0), Pair(-1, -1), Pair(0, 0), Pair(1, -1)),
                                arrayOf(Pair(0, -2), Pair(1, -1), Pair(0, 0), Pair(1, 1)))
        )
    }

    var position = Pair(0, 0)  // Currently not being used. Could be used as offsets to every belonging square.
    var rotation = 0
    var squares = mutableListOf<Square>()

    init {
        position = inPosition

        for (i in 0..3) {
            squares.add(Square(colorsForTypes[type]!!, dimensionsForTypes[type]?.get(i)!!, boardRef))
        }
    }

    private fun getRotatedPositions() : MutableList<Pair<Int, Int>>{

        val rotPositions = mutableListOf<Pair<Int, Int>>()
        for ((index, square) in squares.withIndex()) {
            val offsets = rotationsForTypes[type]?.get(rotation)?.get(index)
            val firstVal = square.position.first + offsets?.first!!
            val secondVal = square.position.second + offsets?.second!!

            // Abort rotation if one square falls out of bounds. Pair (0,0) was chosen arbitrarily.
            if(!boardRef.checkIfPosIsOnBoard(firstVal, secondVal)){
                return mutableListOf(Pair(0, 0))
            }

            rotPositions.add(Pair(firstVal, secondVal))
        }

        return rotPositions
    }

    fun rotate() : Boolean{

        // Get rotated positions
        val positions = getRotatedPositions()
        if (positions == mutableListOf(Pair(0, 0))){
            Log.e("ERROR", "Rotation out of bounds!")
            return false
        }

        // Parse rotated positions
        val options = mutableListOf<Square.Companion.MOVEOPTIONS>()
        for ((index, square) in squares.withIndex()) {
            options.add(square.checkNewPosition(positions[index]))
        }
        for (option in options) {
            if (option == Square.Companion.MOVEOPTIONS.CANT){
                Log.e("ERROR", "Rotation has option CANT!")
                return false
            }
        }

        // Loop rotation back. Tetrominoes rotate by 90 degree angles, so there are only 4 rot states.
        ++rotation
        if (rotation >= 4){
            rotation = 0
        }

        // Actually rotate
        val oldPositions = mutableListOf<Pair<Int, Int>>()
        val newPositions = mutableListOf<Pair<Int, Int>>()
        for ((index, square) in squares.withIndex()) {
            oldPositions.add(square.position)
            square.setNewPosition(positions[index])
            newPositions.add(square.position)
        }

        // Set state positions in board
        for (position in oldPositions){
            boardRef.setStateOnMap(position, Board.Companion.TILESTATE.FREE)
        }
        for (position in newPositions){
            boardRef.setStateOnMap(position, Board.Companion.TILESTATE.NONSTACKED)
        }

        return true
    }

    fun moveAllSquares(unitsToMove : Pair<Int, Int>) : Boolean{
        if (unitsToMove.second == -1){
            Log.e("ERROR", "Tetrominoes cannot move up!")
            return false
        }
        else{

            var rowsToCheck = mutableListOf<Int>()
            val options = mutableListOf<Square.Companion.MOVEOPTIONS>()
            var isGonnaStack = false

            // Parse all square's moving options
            for (square in squares) {
                options.add(square.canMove(unitsToMove))
            }
            for (option in options) {
                if (option == Square.Companion.MOVEOPTIONS.CANT){
                    return false
                }
                else if (option == Square.Companion.MOVEOPTIONS.STACK){
                    isGonnaStack = true
                }
            }

            // Actually move the squares
            val oldPositions = mutableListOf<Pair<Int, Int>>()
            val newPositions = mutableListOf<Pair<Int, Int>>()
            for (square in squares) {
                if (isGonnaStack){
                    square.isStacked = true
                    rowsToCheck.add(square.position.second)
                    boardRef.stackedSquares.add(square)

                    // If trying to stack over tetrominoes already on board, game ends.
                    if (boardRef.checkIfPosIsOccupied(square.position)){
                        boardRef.endGame()
                    }

                    boardRef.setStateOnMap(square.position, Board.Companion.TILESTATE.STACKED)
                }
                else{
                    oldPositions.add(square.position)
                    square.move(unitsToMove)
                    newPositions.add(square.position)
                }
            }

            // Set state positions in board
            for (position in oldPositions){
                boardRef.setStateOnMap(position, Board.Companion.TILESTATE.FREE)
            }
            for (position in newPositions){
                boardRef.setStateOnMap(position, Board.Companion.TILESTATE.NONSTACKED)
            }

            // Extra logic if stack
            if (isGonnaStack){

                val clearedLines = mutableListOf<Int>()
                rowsToCheck = rowsToCheck.distinct().toMutableList()
                rowsToCheck.sortDescending()

                // Clear lines where tetromino landed if needed.
                for (row in rowsToCheck) {
                    if (boardRef.checkIfLineFilled(row)){
                        clearedLines.add(row)
                    }
                }

                if (clearedLines.isNotEmpty()){

                    // Keeping track of score and related vars
                    boardRef.score += Board.scoringSystem[clearedLines.size]!! * boardRef.level
                    boardRef.linesCleared += clearedLines.size
                    boardRef.level = boardRef.linesCleared / 10
                    boardRef.level = ceil(boardRef.level.toDouble()).toInt() + 1

                    Log.d("Score", boardRef.score.toString())
                    Log.d("Lines", boardRef.linesCleared.toString())
                    Log.d("Level", boardRef.level.toString())

                    // Adjust upper lines if lines were cleared
                    for (i in 0 until clearedLines.size) {
                        boardRef.adjustLines(clearedLines[i])
                        for (j in 0 until clearedLines.size) {
                            // As lines are moved down, the initial lines that were cleared also move down, hence the offset.
                            clearedLines[j] += 1
                        }
                    }
                }

                // Spawn new tetromino
                boardRef.replaceCurrentTetromino()
            }

            return true
        }
    }
}

