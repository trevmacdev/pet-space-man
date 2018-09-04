package com.tjgames.games.petspaceman

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get application context
        val myPet = Pet(applicationContext)

        if (!myPet.petExists()){
            val intent = Intent(this, Hatch::class.java)
            startActivity(intent)
            finish()
        } else if (myPet.alive()){
                    val intent = Intent(this, GameLoop::class.java)
                    startActivity(intent)
                    finish()
        }
        else{
            val intent = Intent(this, ResetPet::class.java)
            startActivity(intent)
            finish()
        }
    }
}

