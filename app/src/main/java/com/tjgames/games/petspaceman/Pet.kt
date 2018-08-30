package com.tjgames.games.petspaceman

import android.content.Context
import android.util.Log

open class Pet(applicationContext: Context){

    // set log tag
    private val tag = "Pet::: "

    // establish db connection
    private val dbHelper = DBHelper(applicationContext, null, null, 1)

    val trate = mutableMapOf<String, Int>()
    val m_level = mutableMapOf<String, Int>()
    var c_level = mutableMapOf<String, Int>()



    fun petExists(): Boolean{

        var rs = false
        if (dbHelper.countTableKVP() > 0) rs = true
        Log.i(tag, "Total records in KVP is ${dbHelper.countTableKVP()}")

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

    // populate maps with db stat values
    // use c_level.get("eat") to get a specific clevel value

    private fun selectTrate(){
        trate.put("eat", dbHelper.selectStat("eat","trate" ))
        trate.put("sleep", dbHelper.selectStat("sleep", "trate"))
        trate.put("clean", dbHelper.selectStat("clean", "trate"))
        trate.put("play", dbHelper.selectStat("play", "trate"))

    } //selectTrate

    private fun selectMlevel(){
        m_level.put("eat", dbHelper.selectStat("eat", "mlevel"))
        m_level.put("sleep", dbHelper.selectStat("sleep", "mlevel"))
        m_level.put("clean", dbHelper.selectStat("clean", "mlevel"))
        m_level.put("play", dbHelper.selectStat("play", "mlevel"))
    } //selectMlevel

    private fun selectClevel(){
        c_level.put("eat", dbHelper.selectStat("eat", "clevel"))
        c_level.put("sleep", dbHelper.selectStat("sleep", "clevel"))
        c_level.put("clean", dbHelper.selectStat("clean", "clevel"))
        c_level.put("play", dbHelper.selectStat("play", "clevel"))
    } //selectClevel

    // function to populate all maps and pet variables from database
    // one ring to rule them all
    // should be called each time the game starts, in GameLoop onCreate
    fun initializePet(){
        selectClevel()
        selectMlevel()
        selectTrate()
    } //initializePet

    // update db stats with all c_level values
    fun updateClevel(){
        for ((k, v) in c_level) dbHelper.updateTableCLevel(k, v)
    } //updateClevel

    // setter to update c_level map when action button clicked
    fun setClevel(key: String, mod: Int){

        // determine the new value (v) for c_level[key]
        val v = c_level.get(key)!! + mod

        Log.i(tag, "mlevels are $m_level")
        Log.i(tag, "clevels are $c_level")
        Log.i(tag, "change $key to $v")

        // only modify the value if v is between -1\ and mlevel + 1 (exclusive)
        if (v > -1 && v < m_level.get(key)!! + 1) {
            Log.i(tag, "v is $v which is between -1 and ${m_level.get(key)!!} so updating database")
            // update database
            dbHelper.updateTableCLevel(key, v)

            Log.i(tag, "set database clevel for $key to ${dbHelper.selectStat(key, "clevel")}")

            //TODO: check this for a memory leak
            // update clevel map
            c_level.remove(key)
            c_level.put(key, v)
            Log.i(tag, "updating c_level map: $c_level")
        }
    } //setClevel

    // retrieve the worst stat to determine GameLoop.ivImage
    fun worstStat() : String{

        val worststat = dbHelper.selectLowestCLevel()

        Log.i(tag, "worst stat is $worststat")

        if (c_level.get(worststat) == m_level.get(worststat)) {
            Log.i(tag, "returning happy because ${c_level.get(worststat)} equals ${m_level.get(worststat)}")
            return "happy"
        }
        else {
            Log.i(tag, "returning $worststat because ${c_level.get(worststat)} !equals ${m_level.get(worststat)}")
            return worststat
        }
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
        var i: Int = 0
        c_level.values.forEach { n -> i = i + n }

        if (i < 4) dbHelper.updateTableKVP("alive", "false")
    }












}