package com.example.teamremoteopc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.teamremoteopc.databinding.ActivityGoalBinding

class GoalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalBinding
    private  var goal : String = "food"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserGoal()

        binding.save.setOnClickListener {
            val intent = Intent(this@GoalActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getUserGoal() {
        binding.food.setOnClickListener {

            binding.food.setBackgroundResource(R.drawable.cardbackgroung)

            binding.water.setBackgroundResource(R.drawable.rectangle_outline)

            goal = "food"
        }
        binding.water.setOnClickListener {

            binding.food.setBackgroundResource(R.drawable.rectangle_outline)
            binding.water.setBackgroundResource(R.drawable.cardbackgroung)

            goal = "water"
        }
    }
}