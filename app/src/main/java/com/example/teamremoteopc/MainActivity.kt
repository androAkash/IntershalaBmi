package com.example.teamremoteopc

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.example.teamremoteopc.databinding.ActivityMainBinding
import com.example.teamremoteopc.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var firebaseDatabase : FirebaseDatabase ? = null
    var firebaseAuth : FirebaseAuth ? = null

    private  var gender : String = "male"
    private  var height : Int = 183
    private  var weight : Int = 74
//    private  var age : Int = 24

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserGender()
        getUserHeight()
        getUserWeight()
//        getUserAge()
        onBtnClicked()

        binding.mainActivityAppbarLayout.setOnClickListener {
            val intent = Intent(this,GoalActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth!!.uid!!)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(Users::class.java)
                binding.username.text = "Hello "+userProfile?.getFirstName().toString()
                binding.mainActivityTvAge.text = "dob : "+userProfile?.getAge().toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getUserGender() {
        binding.mainActivityLayoutMale.setOnClickListener {

            binding.mainActivityLayoutFemale.setBackgroundResource(R.drawable.cardbackgroung)

            binding.mainActivityLayoutMale.setBackgroundResource(R.drawable.rectangle_outline)

            gender = "male"
        }

        binding.mainActivityLayoutFemale.setOnClickListener {

            binding.mainActivityLayoutFemale.setBackgroundResource(R.drawable.rectangle_outline)
            binding.mainActivityLayoutMale.setBackgroundResource(R.drawable.cardbackgroung)

            gender = "female"
        }
    }

    private fun getUserHeight() {
        binding.mainActivitySeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.mainActivityTvHeight.text = progress.toString()

                height = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun getUserWeight() {
        binding.mainActivityTvAddWeight.setOnClickListener {
            weight ++

            binding.mainActivityTvWeight.text = weight.toString()
        }

        binding.mainActivityTvDecWeight.setOnClickListener {
            weight--
            binding.mainActivityTvWeight.text = weight.toString()
        }
    }

    private fun navigateToLocation() {
        val intent = Intent(this@MainActivity, LocationActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.location_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.location_activity -> navigateToLocation()
        }

        return true
    }

    private fun onBtnClicked(){
        binding.mainActivityCalculateButton.setOnClickListener {
            showBmiResult()
        }
    }

    private fun showBmiResult() {
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog)

        var imageClose : ImageView = dialog.findViewById(R.id.dialogCloss)
        var bmiValue : TextView = dialog.findViewById(R.id.dialogTvValue)

        imageClose.setOnClickListener {
            dialog.dismiss()
        }

        bmiValue.text = String.format("%1f",calculateBmi())
        dialog.show()
    }

    private fun calculateBmi() : Double {
        val bmi = (weight/(height*height).toDouble())*10000
        return bmi
    }

}