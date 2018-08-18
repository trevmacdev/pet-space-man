package com.tjgames.games.petspaceman

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Hatch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hatch)

        val etPetName = findViewById<EditText>(R.id.etPetName)
        val bnHatch = findViewById<Button>(R.id.bnHatch)
        val tag = "Hatch:::"

        bnHatch.setOnClickListener{

            Log.i(tag, "Petname is ${etPetName.text.length} characters long")

            if (etPetName.text.length == 0){
                Toast.makeText(this, R.string.pet_name, Toast.LENGTH_SHORT).show()
            }
            else{

                // instantiate pet class
                val myPet = Pet(applicationContext)

                // map records for KVP table
                var kvp = mutableMapOf<String, String>()
                kvp.put("pet_name", etPetName.text.toString())
                kvp.put("loop_count", "0")
                kvp.put("loop_max", "100")
                kvp.put("alive", "true")

                // initialize pet database
                myPet.createPet(kvp)

                // call gameloop
                val intent = Intent(this, GameLoop::class.java)
                startActivity(intent)
                finish()
            }

        }


    }
}
