package com.tjgames.games.petspaceman

import android.content.ContentValues
import android.content.Context
import android.util.Log


open class Pet(applicationContext: Context){

    // set log tag
    val tag = "Pet::: "

    // establish db connection
    val dbHelper = DBHelper(applicationContext, null, null, 1)

    fun petExists(): Boolean{

        var rs = false
        if (dbHelper.countTableKVP() > 0) rs = true
        Log.i(tag, "Total records in KVP is ${dbHelper.countTableKVP()}")

        return rs
    }

    fun initialisePet(kvp: MutableMap<String, String>){

        /* This fun is called from Hatch Activity
        Into tableKVP:
        - insert pet name
        - insert state
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
    }

    fun alive() : Boolean{

        val rs = dbHelper.selectKVPValue("alive")
        val r = true
        Log.i(tag, "Pet is alive $rs")
        if(rs == "true"){ val r = true } else{ val r = false }
        return r
    }






}