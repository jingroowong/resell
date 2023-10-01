package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resell.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityForgotPasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val strEmail = binding.forgotEmail.text.toString().trim()

        binding.resetBtn.setOnClickListener{
            if(strEmail.isEmpty()){
                resetPassword()
            }else{
                Toast.makeText(this,"Field cannot be empty",Toast.LENGTH_SHORT).show()
            }
        }






    }

    private fun resetPassword(){
        val strEmail = binding.forgotEmail.text.toString().trim()
        firebaseAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener {
            Toast.makeText(this,"Reset Password link has been sent to your registered Email",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
            .addOnFailureListener{
                Toast.makeText(this,"INVALID EMAIL",Toast.LENGTH_SHORT).show()
            }


    }

}


