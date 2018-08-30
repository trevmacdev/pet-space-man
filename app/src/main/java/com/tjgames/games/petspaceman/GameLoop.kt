package com.tjgames.games.petspaceman

/******* Class Header
 * Start class header
 * Sec 1

 GameLoop is the players main interaction with the pet

 TOC
 Sec 1. Class header
 Sec 2. Imports
 Sec 3. Class variables
 Sec 4. "on" methods
            >   onCreate
                - bnLeft onClick
                - bnRight onCLick
                - bnAction onClick
            >   onResume
                - set pet image
                - set bnAction
                - set looping = true
                - call gameLoop
            >   onPause
                - set looping = false
            >   onDestroy
                - update database
 Sec 5. Helper functions
            >   setActionButton
            >   changeStats
            >   setPetImage
 Sec 6. Game Loop
            > gameLoop
                - fixdedTimerDelay calls loop_action at interval "speed"
                - recall gameLoop if looping is false
            > loopAction
                - update loop_count
                - update pet stats
                - update pet image
                - update alive stat
                - call reset activity is pet is dead

*******
 * End class header
 */

/*******
 * Start imports
 * Sec 2
 */

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
//import android.util.Log
import kotlinx.android.synthetic.main.activity_game_loop.*
import android.os.Handler

/*******
 * End imports
 */

class GameLoop : AppCompatActivity() {

    /*******
     * Start declare class variables
     * Sec 3
     */

    // create log tag
    //private val tag = "GameLoop::: "

    // declare abstract class variables
    private val myPet by lazy { Pet(applicationContext) }
    private var loopCount = 0

    /* determines the focus of bnAction
    1 eat
    2 clean
    3 play
    4 sleep
     */
    private var cAction = 1

    // control game loop
    private var speed: Long = 10000
    private val handler: Handler = Handler()

    /*******
     * End declare class variables
     */

    /*******
     * Start "on" methods
     * Sec 4
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_loop)

        // retrieve myPets values from database
        myPet.initializePet()

        // display pet name in text view
        tvPetName.text = myPet.selectPetName()

        // set click listeners

        bnLeft.setOnClickListener{
            // cycle the cAction values if cAction = 1 -- far left
            cAction = if (cAction == 1) 4
            else cAction - 1
            setActionButton()
        } // bnLeft

        bnRight.setOnClickListener{
            // cycle the cAction values if cAction = 4 -- far right
            cAction = if (cAction == 4) 1
            else cAction + 1
            setActionButton()
        } // bnRight

        bnAction.setOnClickListener{
            changeStats(1) // increase stat focused by cAction by 1
        } // bnAction
    } // onCreate

    override fun onResume() {
        super.onResume()
        // refresh images
        setPetImage()
        setActionButton()
        loopCount = myPet.selectLoopCount()
        handler.postDelayed(gameLoop(), speed)
    } // onResume

    override fun onPause() {
        super.onPause()
        myPet.updateLoopCount(loopCount)
        handler.removeCallbacks(gameLoop())
    } // onPause

    override fun onDestroy() {
        super.onDestroy()
        // save clevel map to database
        myPet.updateClevel()
    } //onDestroy

    /*******
     * End "on" methods
     */

    /*******
     * Start helper functions
     * Sec 5
     */

    private fun setActionButton(){
        // set bnAction image based on the value of cAction
        when (cAction){
            1 -> bnAction.setImageResource(R.drawable.button_eat)
            2 -> bnAction.setImageResource(R.drawable.button_clean)
            3 -> bnAction.setImageResource(R.drawable.button_play)
            4 -> bnAction.setImageResource(R.drawable.button_sleep)
        }
    } // setActionButton

    // modify the clevel stat when bnAction pressed or the game loop runs
    private fun changeStats(mod: Int){
        // change clevel
        when (cAction){
            1 -> myPet.setClevel("eat", mod)
            2 -> myPet.setClevel("clean", mod)
            3 -> myPet.setClevel("play", mod)
            4 -> myPet.setClevel("sleep", mod)
        }
        setPetImage()
    } // changeStats

    // set the pet image based on the worst clevel stat
    private fun setPetImage(){
        // refresh the spaceman image
        when (myPet.worstStat()){
            "eat" -> ivSpaceman.setImageResource(R.drawable.spaceman_eat)
            "clean" -> ivSpaceman.setImageResource(R.drawable.spaceman_clean)
            "play" -> ivSpaceman.setImageResource(R.drawable.spaceman_play)
            "sleep" -> ivSpaceman.setImageResource(R.drawable.spaceman_sleep)
            else -> ivSpaceman.setImageResource(R.drawable.spaceman_happy)
        }
    } // setPetImage

    /*******
     * End helper functions
     */

    /*******
     * Start game loop
     * Sec 6
     */

    private fun gameLoop() : Runnable = Runnable {
        loopAction()
        handler.postDelayed(gameLoop(), speed)
    } //gameLoop

    private fun loopAction() {
        // 1. Increment loop count
        loopCount = loopCount + 1

        // 2. Modify stat if loop_count is wholly divisible by its trate
        if (loopCount % myPet.trate.get("eat")!! == 0) myPet.setClevel("eat", -1)
        if (loopCount % myPet.trate.get("sleep")!! == 0) myPet.setClevel("sleep", -1)
        if (loopCount % myPet.trate.get("play")!! == 0) myPet.setClevel("play", -1)
        if (loopCount % myPet.trate.get("clean")!! == 0) myPet.setClevel("clean", -1)

        // 3. Update pet image
        setPetImage()

        // 4. Update pet alive stat
        myPet.updateAlive(loopCount)

        // 5. Reset game if pet is dead
        if (!myPet.alive()) {
            //TODO create and call reset activity
        }
    } // loopAction

    /*******
     * End game loop
     */

}
