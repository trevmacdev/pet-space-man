package com.tjgames.games.petspaceman

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import android.util.Log

class DBHelper(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){

    val tag = "DBHelper::: "

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
        } catch (e: Exception){
            Log.i(tag, "Failed to insert stats into $TABLE_STATS with exception ${e.message}")
        }finally {
            db.close()
        }
    }

    fun insertKVP(key: String, value: String){

        val values = ContentValues()
        values.put(COLUMN_KEY, key)
        values.put(COLUMN_VALUE, value)

        val db = this.writableDatabase

        try{
            db.insert(TABLE_KV, null, values)

        } catch (e: Exception){
            Log.i(tag, "Failed to insert stats into $TABLE_KV with exception ${e.message}")
        }finally {
            db.close()
        }
    }

    fun selectKVPValue(key: String) : String{

        val db = this.readableDatabase

        var rs: String? = null

        val qry = """
            SELECT $COLUMN_VALUE
            FROM $TABLE_KV
            WHERE $COLUMN_KEY = \"$key\"
        """.trimIndent()

        try {
            val cursor = db.rawQuery(qry, null)
            if (cursor.moveToFirst()){
                cursor.moveToFirst()

                val rs = cursor.getString(0)
                cursor.close()
            }
        } catch (e: Exception){
            rs = "No value"
            Log.i(tag, "Failed to execute query $qry with exception ${e.message}")

        } finally {
            db.close()
            return rs!!
        }
    }

    fun selectStat(stat: String, v: String): Int{

        val db = this.readableDatabase

        var rs: Int? = null

        val qry = """
            SELECT $v
            FROM $TABLE_STATS
            WHERE $COLUMN_STAT
            = \"$stat\"
        """.trimIndent()

        try {
            val cursor = db.rawQuery(qry, null)
            if (cursor.moveToFirst()){
                cursor.moveToFirst()

                val rs = cursor.getInt(0)
                cursor.close()
            }
        } catch (e: Exception){
            rs = 0
            Log.i(tag, "Failed to execute query $qry with exception ${e.message}")

        } finally {
            db.close()
            return rs!!
        }
    }

    fun updateTableKVP(key: String, value: String){

        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_VALUE, value)

        try{
            db.update(TABLE_KV, values, "$COLUMN_KEY = $key", null)
        }catch (e: Exception){
            Log.i(tag, "Failed to update kvp table $key with exception ${e.message}")
        }finally {
            db.close()
        }
    }

    fun updateTableCLevel(stat: String, value: Int){

        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_CLEVEL, value)

        try{
            db.update(TABLE_STATS, values, "$COLUMN_STAT = $stat", null)
        }catch (e: Exception){
            Log.i(tag, "Failed to update stat clevel for $stat with exception ${e.message}")
        }finally {
            db.close()
        }
    }

    fun resetDB(){

        // delete all records from table and vacuum the db

        val db = this.writableDatabase
        var qry = "DELETE FROM $TABLE_STATS"
        db.execSQL(qry)
        qry = "DELETE FROM $TABLE_KV"
        db.execSQL(qry)
        db.execSQL("vacuum")
        db.close()
    }

    // return rows in kvp table
    // used to determine if pet has been initialized or if hatch activity should be called
    fun countTableKVP() : Int{

        val db = this.readableDatabase

        val qry = """
            SELECT COUNT(*)
            FROM $TABLE_KV
        """.trimIndent()

        var rs: Int = 0

        try {
            val cursor = db.rawQuery(qry, null)
            if (cursor.moveToFirst()){
                cursor.moveToFirst()

                val rs = cursor.getInt(0)
                cursor.close()
            }
        } catch (e: Exception){
            rs = 0
            Log.i(tag, "Failed to execute query $qry with exception ${e.message}")

        } finally {
            db.close()
            return rs
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