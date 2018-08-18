package com.tjgames.games.petspaceman

import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.lang.reflect.Modifier


open class Pet(applicationContext: Context){

    // set log tag
    val tag = "Pet::: "

    // establish db connection
    val dbHelper = DBHelper(applicationContext, null, null, 1)

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

        dbHelper.insertStat("eat", 3,3,3)
        dbHelper.insertStat("play", 3, 3, 5)
        dbHelper.insertStat("clean", 3,3,9)
        dbHelper.insertStat("sleep", 3,3,11)
    } //createPet

    // check if pet is alive
    fun alive() : Boolean{

        val rs = dbHelper.selectKVPValue("alive")
        /*val r = true
        Log.i(tag, "Pet is alive: $rs")
        if(rs == "true"){ val r = true } else{ val r = false }*/
        return rs.toBoolean()
    } //alive

    // populate maps with db stat values
    // use c_level.get("eat") to get a specific clevel value

    fun selectTrate(){
        trate.put("eat", dbHelper.selectStat("eat","trate" ))
        trate.put("sleep", dbHelper.selectStat("sleep", "trate"))
        trate.put("clean", dbHelper.selectStat("clean", "trate"))
        trate.put("play", dbHelper.selectStat("play", "trate"))
    } //selectTrate

    fun selectMlevel(){
        m_level.put("eat", dbHelper.selectStat("eat", "mlevel"))
        m_level.put("sleep", dbHelper.selectStat("sleep", "mlevel"))
        m_level.put("clean", dbHelper.selectStat("clean", "mlevel"))
        m_level.put("play", dbHelper.selectStat("play", "mlevel"))
    } //selectMlevel

    fun selectClevel(){
        c_level.put("eat", dbHelper.selectStat("eat", "clevel"))
        c_level.put("sleep", dbHelper.selectStat("sleep", "clevel"))
        c_level.put("clean", dbHelper.selectStat("clean", "clevel"))
        c_level.put("play", dbHelper.selectStat("play", "clevel"))
    } //selectClevel

    // function to populate all maps and pet variables from database
    // should be called each time the game starts, in GameLoop onResume
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

        // determine the new value (v) for c_level[kay]
        val v = c_level.get(key)!! + mod

        // only modify the value if v is between -1 and mlevel + 1 (exclusive)
        if (v > -1 && v < m_level.get(key)!! + 1) {
            // update database
            dbHelper.updateTableCLevel(key, v)

            //TODO: check this for a memory leak
            // update clevel map
            c_level.remove(key)
            c_level.put(key, v)
        }
    } //setClevel

    fun worstStat() : String{

        // get lowest value from c_level map
        var worst_stat = c_level.toList().sortedBy { (_, value) -> value }.toMap().keys.first()

        // if lowest clevel = corresponding mlevel then pet is happy
        if (c_level.getValue(worst_stat) == dbHelper.selectStat(worst_stat, "mlevel"))
            worst_stat = "happy"

        return worst_stat
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