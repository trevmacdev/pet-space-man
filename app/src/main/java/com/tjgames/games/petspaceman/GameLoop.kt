package com.tjgames.games.petspaceman

/*******
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
            >   onDestroy
                - update database
 Sec 5. Helper functions
            >   setActionButton
            >   changeStats
            >   setPetImage


*******
 * End class header
 */

/*******
 * Start imports
 * Sec 2
 */

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

/*******
 * End imports
 */

class GameLoop : AppCompatActivity() {

    /*******
     * Start declare class variables
     * Sec 3
     */

    val myPet = Pet(applicationContext)
    var loop_count = myPet.selectLoopCount()

    /* determines the focus of bnAction
    1 eat
    2 clean
    3 play
    4 sleep
     */
    var cAction = 1

    val bnLeft = findViewById<ImageButton>(R.id.bnLeft)
    val bnRight = findViewById<ImageButton>(R.id.bnRight)
    val bnAction = findViewById<ImageButton>(R.id.bnAction)

    val tvPetName = findViewById<TextView>(R.id.tvPetName)
    val ivSpaceman = findViewById<ImageView>(R.id.ivSpaceman)

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

        // display pet name in text view
        tvPetName.text = myPet.selectPetName()

        bnLeft.setOnClickListener{
            // cycle the cAction values if cAction = 1 -- far left
            if (cAction == 1)
                cAction = 4
            else cAction =- 1
            setActionButton()
        } // bnLeft

        bnRight.setOnClickListener{
            // cycle the cAction values if cAction = 4 -- far right
            if (cAction == 4)
                cAction = 1
            else cAction =+ 1
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
        // TODO start game loop
    }

    override fun onDestroy() {
        super.onDestroy()
        // save loop count to database
        myPet.updateLoopCount(loop_count)

        // save clevel map to database
        myPet.updateClevel()
    }

    /*******
     * End "on" methods
     */

    /*******
     * Start helper functions
     * Sec 5
     */

    fun setActionButton(){
        // set bnAction image based on the value of cAction
        when (cAction){
            1 -> bnAction.setImageResource(R.drawable.button_eat)
            2 -> bnAction.setImageResource(R.drawable.button_clean)
            3 -> bnAction.setImageResource(R.drawable.button_play)
            4 -> bnAction.setImageResource(R.drawable.button_sleep)
        }
    } // setActionButton

    fun changeStats(mod: Int){

        // change clevel
        when (cAction){
            1 -> myPet.setClevel("eat", mod)
            2 -> myPet.setClevel("clean", mod)
            3 -> myPet.setClevel("play", mod)
            4 -> myPet.setClevel("sleep", mod)
        }
        setPetImage()
    } // changeStats

    fun setPetImage(){
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


}
