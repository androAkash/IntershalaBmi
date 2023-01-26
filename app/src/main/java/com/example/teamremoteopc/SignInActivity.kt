package com.example.teamremoteopc

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.teamremoteopc.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        binding.signupRedirectText.setOnClickListener {
            signInToRegister()
        }

        binding.signinButton.setOnClickListener {
            loginUser()
        }

        binding.tvForgetPass.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_forgot,null)
            val userEmail = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                compareEmail(userEmail)
                dialog.dismiss()
            }

            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }

            if (dialog.window != null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()


        }

        setContentView(binding.root)
    }

    private fun loginUser() {
        val email = binding.etSignIn.text.toString()
        val password = binding.signinPassword.text.toString()

        when{
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is required", Toast.LENGTH_SHORT).show()

            else ->{
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("LogIn")
                progressDialog.setMessage("Please wait this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val userAuth : FirebaseAuth = FirebaseAuth.getInstance()

                userAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task->
                    if (task.isSuccessful){
                        progressDialog.dismiss()

                        val intent = Intent(this@SignInActivity,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    private fun signInToRegister(){
        val intent = Intent(this@SignInActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun compareEmail(email:EditText){
        if (email.text.toString().isEmpty()){
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString()).addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(this, "Check Your Email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}