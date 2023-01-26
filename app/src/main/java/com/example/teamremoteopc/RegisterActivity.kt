package com.example.teamremoteopc

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.example.teamremoteopc.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        binding.btnDob.setOnClickListener {
            selectDate()
        }

        binding.signupButton.setOnClickListener{
            createAccount()
        }

        setContentView(binding.root)
    }

    private fun createAccount() {
        val email = binding.signupEmail.text.toString()
        val password = binding.signupPassword.text.toString()
        val firstName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        val contactNumber = binding.contact.text.toString()
        val dateOfBirth = binding.tvDob.text.toString()


        when{
            TextUtils.isEmpty(binding.firstName.toString()) -> Toast.makeText(this, "Please give us your firstname", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(binding.lastName.toString()) -> Toast.makeText(this, "Please give us your lastname", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(binding.signupEmail.toString()) -> Toast.makeText(this, "Please give us your email", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(binding.signupPassword.toString()) -> Toast.makeText(this, "Please give your account a password", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(binding.contact.toString()) -> Toast.makeText(this, "Please give your account a password", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(binding.tvDob.text) -> Toast.makeText(this, "Please select your age", Toast.LENGTH_SHORT).show()

            else ->{
                val progressDialog = ProgressDialog(this@RegisterActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val userAuth : FirebaseAuth = FirebaseAuth.getInstance()
                userAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            saveUserInfo(firstName,lastName,contactNumber,email,dateOfBirth,progressDialog)
                        }
                        else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            userAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(firstName: String, lastName: String, contactNumber: String, email: String, dateOfBirth: String,progressDialog: ProgressDialog) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String,Any>()
        userMap["uid"] = currentUserUid
        userMap["firstName"] = firstName
        userMap["lastName"] = lastName
        userMap["contactNumber"] = contactNumber
        userMap["email"] = email
        userMap["dob"] = dateOfBirth

        userRef.child(currentUserUid).setValue(userMap)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }

    fun selectDate(){
        val c = Calendar.getInstance()
        var cDay = c.get(Calendar.DAY_OF_MONTH)
        var cMonth = c.get(Calendar.MONTH)
        var cYear = c.get(Calendar.YEAR)

        //set calender Dialog
        val calendarDialog = DatePickerDialog(this,DatePickerDialog.OnDateSetListener{
                view,year,month,dayOfMonth ->
            cDay = dayOfMonth
            cMonth = month
            cYear = year
            binding.btnDobCalculator.visibility = View.VISIBLE
            textMessage("You select Date: $cDay/{$cMonth+1}/$cYear")
            binding.btnDobCalculator.setOnClickListener {
                val currentYear = Calendar.getInstance()
                    .get(Calendar.YEAR)
                val age = currentYear - cYear
                binding.tvDob.visibility = View.VISIBLE
                binding.tvDob.text = "Your Age is $age year"
                textMessage( "Your Age is $age year")
            }
            binding.btnDob.text = "$cDay/{$cMonth+1}/$cYear"

        },cYear,cMonth,cDay)
        calendarDialog.show()
    }

    private fun textMessage(s: String) {
        Toast.makeText(this, "$s", Toast.LENGTH_SHORT).show()
    }
}