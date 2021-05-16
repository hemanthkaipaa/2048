package com.hemanthkaipa.madgames.play

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import com.hemanthkaipa.madgames.R
import com.hemanthkaipa.madgames.game.*

class GamePlayActivity : AppCompatActivity() , IGamePlayListener,IGamePlayView,
    View.OnClickListener {

    //UI Components
    lateinit var imageViewPlay : Button
    lateinit var textViewScore : TextView
    lateinit var textViewMessage : TextView
    lateinit var linearLayoutMessage : LinearLayoutCompat
    //variables
    lateinit var arrayList : ArrayList<Int>
    lateinit var gamePlay: GamePlay
    lateinit var styleTile: StyleTile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeUI();
    }

    /**
     * UI Components will be initialized here
     */
    override fun initializeUI() {
        imageViewPlay = findViewById(R.id.imageViewPlay)
        textViewScore = findViewById(R.id.textViewScore)
        textViewMessage = findViewById(R.id.textViewMessage)
        linearLayoutMessage = findViewById(R.id.linearLayoutMessage)

        initializeObjects()
        initializeListeners()
        initTiles()

    }

    /**
     * Object creation takes place here
     */
    override fun initializeObjects() {
        arrayList = ArrayList()
        gamePlay = GamePlay(this)
        styleTile = StyleTile(this)
    }

    /**
     * This method will invoke all required initializers ex : Button click,spinners etc
     */
    override fun initializeListeners() {
        imageViewPlay.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageViewPlay ->{
                try {
                    if (linearLayoutMessage.visibility==View.VISIBLE)linearLayoutMessage.visibility=View.INVISIBLE
                    gamePlay.initNewGame()
                } catch (e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * This methos gets called when user performs any touch action on Screen and is used to handle
     * touch events like left,right,top,bottom etc
     *
     */
    // x1,x2,y1,y2 touch event coordinates of the screen and used to record and calculate
    var x1 = 0f
    var y1 = 0f
    var x2 = 0f
    var y2 = 0f
    // distance is minimum slide distance used for calculation
    var distance = 150
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!=null) {
            when(event.action){

                MotionEvent.ACTION_DOWN ->{
                    x1 = event.x
                    y1 = event.y
                }
                MotionEvent.ACTION_UP ->{
                    x2 = event.x
                    y2 = event.y
                    onTouchEventResult()
                }

            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * This method will calculate the directions from result of x and y axis
     */
    private fun onTouchEventResult(){
        try {
            when {
                onSlideRight() -> {
                    gamePlay.actionSlide(Moves.Right)
                }
                onSlideLeft() -> {
                    gamePlay.actionSlide(Moves.Left)
                }
                onSlideUp() -> {
                    gamePlay.actionSlide(Moves.Up)
                }
                onSlideDown() -> {
                    gamePlay.actionSlide(Moves.Down)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * Method invoke touchEvent Slide Left to Right
     */
    private fun onSlideRight(): Boolean {
        return (x2 > x1 && Math.abs(x2 - x1) > distance)
    }
    /**
     * Method invoke touchEvent Slide Right to Left
     */
    private fun onSlideLeft(): Boolean{
        return (x2 < x1 && Math.abs(x1 - x2) > distance)
    }
    /**
     * Method invoke touchEvent Slide Down to Up
     */
    private fun onSlideUp(): Boolean {
        return (y1 > y2 && Math.abs(y1 - y2) > distance)
    }
    /**
     * Method invoke touchEvent Slide Up to Down
     */
    private fun onSlideDown(): Boolean {
        return (y2 > y1 && Math.abs(y2 - y1) > distance)
    }

    /**
     * invoked by an interface when there is a change in tile/game board where user reached the target
     * score resulting winning the Game and presenting a trophy to the winner
     */
    override fun wonGame() {
        if (linearLayoutMessage.visibility==View.INVISIBLE)linearLayoutMessage.visibility=View.VISIBLE
        textViewMessage.text = getString(R.string.won_game)
    }

    /**
     * invoked by an interface when there is a change in tile/game board where user lost out of moves
     * which will result of loosing the game though user will get a Trophy for Playing the game. LOL
     */
    override fun lostGame() {
        if (linearLayoutMessage.visibility==View.INVISIBLE)linearLayoutMessage.visibility=View.VISIBLE
        textViewMessage.text = getString(R.string.lose_game)
    }

    /**
     * invoked by an interface when there is a change in tile/game board where user score will be updated
     */
    override fun UpdateScore(score: Int) {
        textViewScore.text = score.toString()
    }

    /**
     * invoked by interface when there is a change in value which will result the value updation
     */
    override fun updateTileValue(action: GameMoveRecord) {
        drawGridLayout(action)
    }

    /**
     * This invoice when there is a action change / tile change and it will update the text and style
     * w.r.t the value of the tile
     */
    private fun drawGridLayout(action : GameMoveRecord){
        try {
            val textView  = findViewById<TextView>(arrayList.get(action.tile_location))
            if (action.action == ActionTile.Slide || action.action == ActionTile.Pair) {
                styleTile.styleTextView(textView, action.value)
                this.drawGridLayout(GameMoveRecord(ActionTile.Clear, 0, action.tile_oldLocation,-1))
            } else {
                styleTile.styleTextView(textView, action.value)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * Initiating the tile views into an arraylist which will be used further
     */
    private fun initTiles(){
        arrayList.clear()
        arrayList.add(R.id.textView1)
        arrayList.add(R.id.textView2)
        arrayList.add(R.id.textView3)
        arrayList.add(R.id.textView4)
        arrayList.add(R.id.textView5)
        arrayList.add(R.id.textView6)
        arrayList.add(R.id.textView7)
        arrayList.add(R.id.textView8)
        arrayList.add(R.id.textView9)
        arrayList.add(R.id.textView10)
        arrayList.add(R.id.textView11)
        arrayList.add(R.id.textView12)
        arrayList.add(R.id.textView13)
        arrayList.add(R.id.textView14)
        arrayList.add(R.id.textView15)
        arrayList.add(R.id.textView16)
    }
}