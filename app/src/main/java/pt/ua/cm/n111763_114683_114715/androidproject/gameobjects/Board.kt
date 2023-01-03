package pt.ua.cm.n111763_114683_114715.androidproject.gameobjects

import android.util.Log
import kotlin.random.Random

class Board {
    // Companion object is equivalent to static keyword in other languages
    companion object{
        const val width = 10
        const val height = 20
        var tileSize = 80

        enum class TILESTATE {
            FREE,
            NONSTACKED,
            STACKED
        }

        // Multiplier according to lines cleared at a time.
        val scoringSystem = mapOf(
            1 to 100,
            2 to 300,
            3 to 500,
            4 to 800
        )
    }

    var boardMap = arrayOf(
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        ),
        arrayOf(
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE,
            TILESTATE.FREE
        )
    )

    lateinit var currentTetromino : Tetromino
    var stackedSquares = mutableListOf<Square>()
    var tetrominoTypesBag = mutableListOf<String>()
    var tetrominoBag = mutableListOf<Tetromino>()

    var score = 0
    var linesCleared = 0
    var level = 1
    var gameOver = false

    init {
        replaceCurrentTetromino()
    }

    fun endGame(){
        gameOver = true
    }

    // For debug purposes only.
    fun printBoard(){
        for (i in boardMap.indices) {

            var tempString = ""
            for (state in boardMap[i]){
                when (state) {
                    TILESTATE.FREE -> tempString += "O"
                    TILESTATE.NONSTACKED -> tempString += "H"
                    TILESTATE.STACKED -> tempString += "X"
                }
            }

            tempString += i.toString()
            Log.d("DEBUG", tempString)
        }

        Log.d("DEBUG", "END")
    }

    fun moveTetromino(unitsToMove : Pair<Int, Int>){
        currentTetromino.moveAllSquares(unitsToMove)
    }

    fun rotateTetromino(){
        currentTetromino.rotate()
    }

    fun checkIfLineFilled(rowNmbr : Int) : Boolean{

        // Actually doing the checking.
        for (state in boardMap[rowNmbr]){
            if (state != TILESTATE.STACKED){
                return false
            }
        }

        // Removing the line.
        val squaresToRemove = mutableListOf<Square>()
        for (square in stackedSquares){
            if (square.position.second == rowNmbr){
                squaresToRemove.add((square))
                setStateOnMap(square.position, TILESTATE.FREE)
            }
        }
        for (square in squaresToRemove){
            stackedSquares.remove(square)
        }

        return true
    }

    fun adjustLines(minimumRow : Int){

        val squaresToMove = mutableListOf<Square>()
        val oldPositions = mutableListOf<Pair<Int, Int>>()
        val newPositions = mutableListOf<Pair<Int, Int>>()

        for (square in stackedSquares){
            if (square.position.second <= minimumRow){
                squaresToMove.add(square)
            }
        }
        for (square in squaresToMove){
            oldPositions.add(square.position)
            square.move(Pair(0, 1))
            newPositions.add(square.position)
        }

        // Refresh positions.
        for (position in oldPositions){
            setStateOnMap(position, TILESTATE.FREE)
        }
        for (position in newPositions){
            setStateOnMap(position, TILESTATE.STACKED)
        }
    }

    fun checkIfPosIsOccupied(posToCheck: Pair<Int, Int>) : Boolean{
        if (checkIfPosIsOnBoard(posToCheck.first, posToCheck.second)){
            return boardMap[posToCheck.second][posToCheck.first] == TILESTATE.STACKED
        }

        return true
    }

    fun checkIfPosIsOnBoard(firstVal : Int, secondVal : Int) : Boolean{
        // If values are not the same, it means the clamp was put into action.
        if (firstVal.coerceIn(0, width - 1) == firstVal && secondVal.coerceIn(0, height - 1) == secondVal){
            return true
        }
        else {
            Log.e("ERROR", "Trying to move outside of range")
            return false
        }
    }

    fun setStateOnMap(posToSet : Pair<Int, Int>, stateToSet : TILESTATE){
        if (checkIfPosIsOnBoard(posToSet.first, posToSet.second)){
            boardMap[posToSet.second][posToSet.first] = stateToSet
        }
    }

    fun getIsAdjacentFree(posToCheck : Pair<Int, Int>): Array<TILESTATE>{
        // Order is: left, right, bottom.
        val adjacentState = arrayOf(TILESTATE.FREE, TILESTATE.FREE, TILESTATE.FREE)
        val leftPos = Pair(posToCheck.first - 1, posToCheck.second)
        val rightPos = Pair(posToCheck.first + 1, posToCheck.second)
        val bottomPos = Pair(posToCheck.first, posToCheck.second + 1)

        // Checking left
        if(leftPos.first < 0){
            adjacentState[0] = TILESTATE.STACKED
        }
        else{
            adjacentState[0] = boardMap[leftPos.second][leftPos.first]
        }

        // Checking right
        if(rightPos.first >= width){
            adjacentState[1] = TILESTATE.STACKED
        }
        else{
            adjacentState[1] = boardMap[rightPos.second][rightPos.first]
        }

        // Checking bottom
        if(bottomPos.second >= height){
            adjacentState[2] = TILESTATE.STACKED
        }
        else{
            adjacentState[2] = boardMap[bottomPos.second][bottomPos.first]
        }

        return adjacentState
    }

    fun replaceCurrentTetromino(){
        if (tetrominoBag.isEmpty()){

            // Refill types bag
            for (type in Tetromino.types) {
                tetrominoTypesBag.add(type)
            }

            // Empty types bag while randomly filling tetromino bag.
            for (i in 0 until tetrominoTypesBag.size) {
                val randomIndex = Random.nextInt(tetrominoTypesBag.size)
                val randomType = tetrominoTypesBag[randomIndex]
                tetrominoBag.add(Tetromino(Pair(0, 0), randomType, this))
                tetrominoTypesBag.remove(randomType)
            }
        }

        if (!gameOver){
            // Bag system is standard random piece generator according to Tetris guidelines.
            currentTetromino = tetrominoBag.last()
            tetrominoBag.removeLast()
        }
    }
}