package pt.ua.cm.n111763_114683_114715.androidproject


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.OnClickListener
import android.widget.*

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentPlayBinding

class PlayFragment : Fragment(), OnClickListener {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentPlayBinding
    private lateinit var firestore: FirebaseFirestore

    private lateinit var gameView: GameView
    private lateinit var leftButton: ImageButton
    private lateinit var downButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var rotateButton: ImageButton

    private var doOnce = true
    private var gameBoard = Board()

    private var fps : Int = 30

    private var fallSpeed : Int = fps
    private var initFallSpeed : Int = fallSpeed
    private var hardDropdownSpeed : Int = fallSpeed/15  //Speed when holding down
    private var fallSpeedCounter : Int = 0

    private var moveSpeed : Int = fps/13  //Speed when holding left/right
    private var initMoveSpeed : Int = moveSpeed
    private var moveDelay : Int = fps/3  //Delay between pressing button and piece moving at moveSpeed while holding
    private var moveSpeedCounter : Int = 0
    private var moveDirection = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        firestore = FirebaseFirestore.getInstance()

        gameView = binding.myGameView
        leftButton = binding.leftButton
        downButton = binding.downButton
        rightButton = binding.rightButton
        rotateButton = binding.rotateButton

        rotateButton.setOnClickListener(this)


        // Input handling
        leftButton.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    moveButtonAction(-1)
                }
                MotionEvent.ACTION_UP -> {
                    moveDirection = 0
                    moveSpeed = moveDelay
                }
            }
            return@OnTouchListener true
        })

        downButton.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    fallSpeed = hardDropdownSpeed
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("Speed", gameBoard.level.toString())
                    fallSpeed = initFallSpeed / gameBoard.level
                }
            }
            return@OnTouchListener true
        })

        rightButton.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    moveButtonAction(1)
                }
                MotionEvent.ACTION_UP -> {
                    moveDirection = 0
                    moveSpeed = moveDelay
                }
            }
            return@OnTouchListener true
        })




        // Setup
        gameView.setup(gameBoard)

        // Get size of GameView to adjust board's tile scale
        gameView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (doOnce){
                    doOnce = false
                    val width : Int = gameView.width / Board.width
                    val height : Int = gameView.height / Board.height
                    if (width < height){
                        Board.tileSize = width
                    }
                    else{
                        Board.tileSize = height
                    }
                }
            }
        })

        // Start loop
        startLoop()
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rotateButton -> rotate()
        }
    }

    private fun rotate(){
        gameBoard.rotateTetromino()
    }


    private fun moveButtonAction(direction : Int){
        moveDirection = direction
        gameBoard.moveTetromino(Pair(moveDirection, 0))
        gameView.invalidate()

        moveSpeedCounter = 0
        moveSpeed = moveDelay
    }



    private fun startLoop() {
        CoroutineScope(Dispatchers.IO).launch {

            val delayTime : Long = (1000/fps).toLong()

            while (!gameBoard.gameOver) {
                ++fallSpeedCounter
                ++moveSpeedCounter

                // Check for horizontal input
                if (moveSpeedCounter >= moveSpeed){
                    moveSpeedCounter = 0

                    if (moveDirection != 0){
                        moveSpeed = initMoveSpeed
                        gameBoard.moveTetromino(Pair(moveDirection, 0))
                    }
                }

                // Check for vertical input
                if (fallSpeedCounter >= fallSpeed){
                    fallSpeedCounter = 0
                    gameBoard.moveTetromino(Pair(0, 1))
//                    txtScoreValue.text = gameBoard.score.toString()
//                    txtLinesClrdValue.text = gameBoard.linesCleared.toString()
//                    txtLevelValue.text = gameBoard.level.toString()
                }

                gameView.invalidate()
                delay(delayTime)
            }

            Log.d("DEBUG", "OutOfTheLoop")
        }
    }
}