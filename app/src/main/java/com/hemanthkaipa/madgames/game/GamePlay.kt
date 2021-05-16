package com.hemanthkaipa.madgames.game

import com.hemanthkaipa.madgames.utils.Utils
import java.io.Serializable
import kotlin.random.Random

enum class Moves{Left,Right,Up,Down}
enum class ActionTile{Add,Clear,Pair,Slide}
data class GameMoveRecord(var action : ActionTile,
                          var value :Int,
                          var tile_location : Int,
                          var tile_oldLocation : Int) : Serializable

class GamePlay(listenerI: IGamePlayListener) : Serializable{
    //interface
    private var gamePlayListener  = listenerI;
    //Lists
    /**
     * track of moves stored in movesArrayList
     */
    private var movesArrayList : ArrayList<GameMoveRecord> = ArrayList()
    private var tileArrayList : ArrayList<Int> = ArrayList()
    //Variables
    private val boardTileCount = Utils.BOARD_TILE_COUNT
    private val rowCount = Utils.BOARD_DIMENSIONS
    private val columnCount = Utils.BOARD_DIMENSIONS
    private val blankTile = Utils.DEFAULT_TILE_VALUE
    var score = 0
        private set
    var maxTile = Utils.DEFAULT_TILE_VALUE
        private set
    private var gameOver = false
    private var emptyTiles = Utils.BOARD_TILE_COUNT


    /**
     * Initializing new game where all moves/tiles will be cleared / reset to default and
     * default tiles will be added in random position
     *
     */
    open fun initNewGame(){
        this.emptyTiles = Utils.BOARD_TILE_COUNT
        this.maxTile = Utils.DEFAULT_TILE_VALUE
        this.gameOver = false
        this.score = 0
        this.movesArrayList = ArrayList()
        this.tileArrayList = ArrayList()

        for(i in 0 until boardTileCount){
            tileArrayList.add(blankTile)
            movesArrayList.add(GameMoveRecord(ActionTile.Clear,blankTile,i,-1))
        }
        addTile(2)
        addTile(4)
        updateGameTransaction();
    }

    /**
     * This will update the Game Moves which ever stored in moves ArrayList
     */
    private fun updateGameTransaction(){
        try {
            for(item in movesArrayList){
                gamePlayListener.updateTileValue(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Adds a new tile when beginning of the game and while playing game which creates a tile value
     * 2 or 4 based on random ratio
     * For Adding Tile it will check the available blank tiles with randam position and assign a value
     * to the tile
     */
    fun addTile(initialValue : Int = 0){
        var value :Int = initialValue
        if (value!=2&&value!=4) {
            var randomInt = Random.nextInt(100)
            if (randomInt>=80){
                value = 4
            }else{
                value = 2
            }
        }
        val randomCellPosition = Random.nextInt(emptyTiles)
        var availableTiles =0;
        for(i in 0 until boardTileCount){
            if (tileArrayList[i] ==blankTile){
                if (availableTiles==randomCellPosition){
                    tileArrayList[i] = value
                    if (value > maxTile) { maxTile = value }
                    emptyTiles-=1;
                    movesArrayList.add(GameMoveRecord(ActionTile.Add,value,i,-1))
                    break
                }
                availableTiles+=1;
            }
        }
    }

    /**
     * This method invokes if any action performed by user with Touchable interaction any of
     * four directions ex: actionLeft,Right,Up,Down
     */
    fun actionSlide(move : Moves) : Boolean{
        var tempScore = this.score
        this.movesArrayList = ArrayList()
        val changed = when (move) {
            Moves.Up -> actionSlideUp()
            Moves.Down -> actionSlideDown()
            Moves.Left -> actionSlideLeft()
            Moves.Right -> actionSlideRight()
        }
        // if changed add new Tile & update title
        if (changed) {
            addTile()
            updateGameTransaction()
        }
        // update user score
        if (this.score != tempScore) {
            gamePlayListener.UpdateScore(score = this.score)
        }
        // check if can we move or not
        if (!areMovesAvailable()) {
            this.gameOver = true
            if (this.maxTile >= Utils.TARGET) {
                gamePlayListener.wonGame()
            } else {
                gamePlayListener.lostGame()
            }
            return false
        }
        return changed
    }

    /**
     * Invoked when performed action left
     * 1) It will compute elements available 0,4,8,12 followed by indexes
     * 2) It will check and pair the matched tiles by same value
     * 3) After computing and pairing the elements it will again compute and check whether blank tiles
     * and allow sliding of elements
     */
    private fun actionSlideLeft() : Boolean {
        val a = slideLeft()
        val b = pairLeft()
        val c = slideLeft()
        return (a || b || c )
    }
    /**
     * Invoked when performed action right
     * 1) It will compute elements available 12, 8, 4, 0 followed by indexes
     * 2) It will check and pair the matched tiles by same value
     * 3) After computing and pairing the elements it will again compute and check whether blank tiles
     * and allow sliding of elements
     */
    private fun actionSlideRight() : Boolean {
        val a = slideRight()
        val b = pairRight()
        val c = slideRight()
        return (a || b || c )
    }
    /**
     * Invoked when performed action right
     * 1) It will compute elements available 0, 1, 2, 3 followed by indexes
     * 2) It will check and pair the matched tiles by same value
     * 3) After computing and pairing the elements it will again compute and check whether blank tiles
     * and allow sliding of elements
     */
    private fun actionSlideUp() : Boolean {
        val a = slideUp()
        val b = pairUp()
        val c = slideUp()
        return (a || b || c )
    }
    /**
     * Invoked when performed action right
     * 1) It will compute elements available 3, 2, 1, 0 followed by indexes
     * 2) It will check and pair the matched tiles by same value
     * 3) After computing and pairing the elements it will again compute and check whether blank tiles
     * and allow sliding of elements
     */
    private fun actionSlideDown() : Boolean {
        val a = slideDown()
        val b = pairDown()
        val c = slideDown()
        return (a || b || c )
    }

    /**
     * This method will return a boolean whether if we can move furthur or not
     * Also after filling Last empty tile it will check any pairable elements exist to pair or not
     * then it will return a boolean
     */
    private fun areMovesAvailable() : Boolean {
        if (emptyTiles > 0) { return true }
        val array1 = boardTileCount-columnCount
        for (i in 0 until array1) {
            if (tileArrayList[i] == tileArrayList[i + columnCount]) { return true }
        }
        val array2 = boardTileCount - 1
        for (i in 0 until array2) {
            if ((i + 1) % rowCount > 0) {
                if (tileArrayList[i] == tileArrayList[i + 1]) { return true }
            }
        }
        return false
    }

    /**
     * This method performs sliding operation where indexes will be used to fill the blank tiles
     */
    private fun move(vararg indexes: Int) : Boolean {
        var moved = false
        val indexesArray = intArrayOf(*indexes)
        var empty = 0
        for (j in indexesArray.indices) {
            /**
             * ex : List[array[varInt]] will start checking from self tile by incrementing varInt
             * if edge or no blank field and it will swap/move the indexes
             */
            if (tileArrayList[indexesArray[empty]] != blankTile) {
                empty += 1
                continue
            } else if (tileArrayList[indexesArray[j]] == blankTile) {
                continue
            } else {
                // Otherwise we have a slide condition
                tileArrayList[indexesArray[empty]] = tileArrayList[indexesArray[j]]
                tileArrayList[indexesArray[j]] = blankTile
                movesArrayList.add(GameMoveRecord(ActionTile.Slide, tileArrayList[indexesArray[empty]], indexesArray[empty], indexesArray[j]))
                moved = true
                empty += 1
            }
        }
        return moved
    }

    /**
     * This method will invoke to pair near by equivalent elements with the help of indexes
     */
    private fun pair(vararg indexes: Int) : Boolean {
        var paired = false
        val indexesArray = intArrayOf(*indexes)

        for (index in 0 until (indexesArray.size-1)) {
            /**
             * here we will be able to pair with nearby similar elements
             * ex: when RTL scenario list[array[index($index)]] will give array of indexes 0,4,8,12
             * if 0th index having a number 16 then check for list[array[index($index+1)]] which is the
             * index value of 4 containing number 16 if matched multiply * 2 and assign the value to
             *  0th index and blank value to 4th Index
             */
            if (tileArrayList[indexesArray[index]] != blankTile && tileArrayList[indexesArray[index]] == tileArrayList[indexesArray[index+1]]) {
                val pairedTileValue = tileArrayList[indexesArray[index]] * 2
                tileArrayList[indexesArray[index]] = pairedTileValue
                tileArrayList[indexesArray[index+1]] = blankTile
                score += pairedTileValue
                if (pairedTileValue > maxTile) {
                    maxTile = pairedTileValue
                }
                movesArrayList.add(GameMoveRecord(ActionTile.Pair, pairedTileValue, indexesArray[index], indexesArray[index+1]))
                paired = true
                emptyTiles += 1
            }
        }
        return paired
    }

    /**
     * This method will gather the indexes in board based on the action performed Left
     * it will consider all 4 sides as 4X4 Grid
     */
    private fun slideLeft() : Boolean {
        val a = move(0, 4, 8, 12)
        val b = move(1, 5, 9, 13)
        val c = move(2, 6, 10, 14)
        val d = move(3, 7, 11, 15)
        return (a || b || c || d)
    }
    /**
     * This method will gather the indexes in board based on the action performed Right
     * it will consider all 4 sides as 4X4 Grid
     */
    private fun slideRight() : Boolean {
        val a = move(12, 8, 4, 0)
        val b = move(13, 9, 5, 1)
        val c = move(14, 10, 6, 2)
        val d = move(15, 11, 7, 3)
        return (a || b || c || d)
    }
    /**
     * This method will gather the indexes in board based on the action performed Upwards
     * it will consider all 4 sides as 4X4 Grid
     */
    private fun slideUp() : Boolean {
        val a = move(0, 1, 2, 3)
        val b = move(4, 5, 6, 7)
        val c = move(8, 9, 10, 11)
        val d = move(12, 13, 14, 15)
        return (a || b || c || d)
    }

    /**
     * This method will gather the indexes in board based on the action performed Downwards
     * it will consider all 4 sides as 4X4 Grid
     */
    private fun slideDown() : Boolean {
        val a = move(3, 2, 1, 0)
        val b = move(7, 6, 5, 4)
        val c = move(11, 10, 9, 8)
        val d = move(15, 14, 13, 12)
        return (a || b || c || d)
    }

    /**
     * This method will be used to measuring the tiles for pairing and will
     * gather the indexes in board based on the action performed Left
     * it is equivalent to $slideLeft fun.
     */
    private fun pairLeft() : Boolean {
        val a = pair(0, 4, 8, 12)
        val b = pair(1, 5, 9, 13)
        val c = pair(2, 6, 10, 14)
        val d = pair(3, 7, 11, 15)
        return (a || b || c || d)
    }
    /**
     * This method will be used to measuring the tiles for pairing and will
     * gather the indexes in board based on the action performed Right
     * it is equivalent to $slideRight fun.
     */
    private fun pairRight() : Boolean {
        val a = pair(12, 8, 4, 0)
        val b = pair(13, 9, 5, 1)
        val c = pair(14, 10, 6, 2)
        val d = pair(15, 11, 7, 3)
        return (a || b || c || d)
    }
    /**
     * This method will be used to measuring the tiles for pairing and will
     * gather the indexes in board based on the action performed Up
     * it is equivalent to $slideUp fun.
     */
    private fun pairUp() : Boolean {
        val a = pair(0, 1, 2, 3)
        val b = pair(4, 5, 6, 7)
        val c = pair(8, 9, 10, 11)
        val d = pair(12, 13, 14, 15)
        return (a || b || c || d)
    }
    /**
     * This method will be used to measuring the tiles for pairing and will
     * gather the indexes in board based on the action performed Down
     * it is equivalent to $slideDown fun.
     */
    private fun pairDown() : Boolean {
        val a = pair(3, 2, 1, 0)
        val b = pair(7, 6, 5, 4)
        val c = pair(11, 10, 9, 8)
        val d = pair(15, 14, 13, 12)
        return (a || b || c || d)
    }
}