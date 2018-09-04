package com.tjgames.games.petspaceman

import android.content.Context
//import android.util.Log

open class Pet(applicationContext: Context){

    // set log tag
    //private val tag = "Pet::: "

    // establish db connection
    private val dbHelper = DBHelper(applicationContext, null, null, 1)

    // get static values from database (trate)

    val eat_trate by lazy { dbHelper.selectStat("eat","trate") }
    val play_trate by lazy { dbHelper.selectStat("play","trate") }
    val clean_trate by lazy { dbHelper.selectStat("clean","trate") }
    val sleep_trate by lazy { dbHelper.selectStat("sleep","trate") }

    fun petExists(): Boolean{
        var rs = false
        if (dbHelper.countTableKVP() > 0) rs = true
        return rs
    } // petExists

    fun createPet(kvp: MutableMap<String, String>){

        /* This fun is called from Hatch Activity
        Into tableKVP:
        - insert pet name
        - insert alive
        - insert loop count
        - insert loop max

        Into tablestats
        - insert eat
        - insert play
        - insert sleep
        - insert clean
         */

        for ((k,v) in kvp){
            dbHelper.insertKVP(k, v)
        }

        dbHelper.insertStat("eat", 3,3,2)
        dbHelper.insertStat("play", 3, 3, 3)
        dbHelper.insertStat("clean", 3,3,5)
        dbHelper.insertStat("sleep", 3,3,11)
    } //createPet

    // check if pet is alive
    fun alive() : Boolean{
        return dbHelper.selectKVPValue("alive").toBoolean()
    } //alive

    // setter to update c_level map when action button clicked
    fun setClevel(stat: String, mod: Int){

        // determine the new value (v) for c_level[key]
        val v = dbHelper.selectStat(stat, "clevel") + mod

        // only modify the value if v is between -1\ and mlevel + 1 (exclusive)
        if (v > -1 && v < 4) {
            // update database
            dbHelper.updateTableCLevel(stat, v)
        }
    } //setClevel

    // retrieve the worst stat to determine GameLoop.ivImage
    fun worstStat() : String{

        val worststat = dbHelper.selectLowestCLevel()

        /*if (c_level.get(worststat) == m_level.get(worststat)) {
            return "happy"
        }
        else {
            return worststat
        }*/

        if (dbHelper.selectStat(worststat, "clevel") == dbHelper.selectStat(worststat, "mlevel"))
            return "happy"
        else
            return worststat

    } //worstStat

    fun selectLoopCount() : Int {
        return dbHelper.selectKVPValue("loop_count").toInt()
    }

    fun updateLoopCount(loop_count: Int){
        dbHelper.updateTableKVP("loop_count", loop_count.toString())
    }

    fun selectPetName() : String{
        return dbHelper.selectKVPValue("pet_name")
    }

    fun updateAlive(game_loop : Int){

        // Check if pet has died of old age
        if (game_loop > dbHelper.selectKVPValue("loop_max").toInt()) {
            dbHelper.updateTableKVP("alive", "false")
            return
        }

        // Check if pet has died from neglect

        if (dbHelper.selectStat("eat", "clevel") +
                dbHelper.selectStat("play", "clevel") +
                dbHelper.selectStat("clean", "clevel") +
                dbHelper.selectStat("sleep", "clevel") < 2)
            dbHelper.updateTableKVP("alive", "false")


    }












}