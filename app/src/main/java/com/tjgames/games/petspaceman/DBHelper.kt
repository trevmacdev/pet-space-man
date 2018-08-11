package com.tjgames.games.petspaceman

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import android.util.Log

class DBHelper(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){

    val tag = "DBHelper"

    override fun onCreate(db: SQLiteDatabase) {

        val create_stat_table = """
            CREATE TABLE $TABLE_STATS(
            $COLUMN_STAT TEXT PRIMARY KEY,
            $COLUMN_CLEVEL INTEGER,
            $COLUMN_MLEVEL INTEGER,
            $COLUMN_TRATE INTEGER,)
        """.trimIndent()

        val create_kv_table = """
            CREATE TABLE $TABLE_KV(
            $COLUMN_KEY TEXT PRIMARY KEY,
            $COLUMN_VALUE TEXT)
        """.trimIndent()

        try{
            db.execSQL(create_stat_table)
        }
        catch(e: Exception){
            Log.i(tag, "Failed to execute $create_stat_table with exception ${e.message}")
        }

        try{
            db.execSQL(create_kv_table)
        }
        catch(e: Exception){
            Log.i(tag, "Failed to execute $create_kv_table with exception ${e.message}")
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        /* Right now I have no reason to do this
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STATS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_KV")
        onCreate(db)
        */
    }

    // INSERT STATEMENTS

    fun insertStat(stat: String, clevel: Int, mlevel: Int, trate: Int){

        val values = ContentValues()
        values.put(COLUMN_STAT, stat)
        values.put(COLUMN_CLEVEL, clevel)
        values.put(COLUMN_MLEVEL, mlevel)
        values.put(COLUMN_TRATE, trate)

        val db = this.writableDatabase

        try{
            db.insert(TABLE_STATS, null, values)
            db.close()
        } catch (e: Exception){
            Log.i(tag, "Failed to insert stats into $TABLE_STATS with exception ${e.message}")
        }
    }

    fun insertkvp(key: String, value: String){

        val values = ContentValues()
        values.put(COLUMN_KEY, key)
        values.put(COLUMN_VALUE, value)

        val db = this.writableDatabase

        try{
            db.insert(TABLE_KV, null, values)
            db.close()
        } catch (e: Exception){
            Log.i(tag, "Failed to insert stats into $TABLE_KV with exception ${e.message}")
        }
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "pet.db"

        val TABLE_STATS = "tablestats"
        val COLUMN_STAT = "stat"
        val COLUMN_CLEVEL = "clevel"
        val COLUMN_MLEVEL = "mlevel"
        val COLUMN_TRATE = "trate"

        val TABLE_KV = "tablekvp"
        val COLUMN_KEY = "key"
        val COLUMN_VALUE = "value"
    }

}