package com.example.resell


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resell.R
import com.example.resell.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  reference: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val signInButton = findViewById<ImageView>(R.id.googleBtn)
        signInButton.setOnClickListener {
            val intent = Intent(this, GoogleActivity::class.java)
            startActivity(intent)
        }



        firebaseAuth = FirebaseAuth.getInstance()

        binding.logBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val pass = binding.logPass.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, CoverPage::class.java)
                        startActivity(intent)
                        Toast.makeText(this,  "Login Successfully", Toast.LENGTH_SHORT).show()
                    }
                }


            } else {
                Toast.makeText(this, "Empty Fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.regLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPass.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }





        binding.btnTwitter.setOnClickListener {
            val intent = Intent(this, TwitterActivity::class.java)
            startActivity(intent)
        }

        binding.btnYahoo.setOnClickListener{
            val provider = OAuthProvider.newBuilder("yahoo.com")
            provider.addCustomParameter("prompt", "login")
            provider.addCustomParameter("language", "en")

            val pendingResultTask = firebaseAuth.pendingAuthResult
            if (pendingResultTask != null) {

                pendingResultTask
                    .addOnSuccessListener {

                        val intent = Intent(this, CoverPage::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Try Again ", Toast.LENGTH_SHORT).show()
                    }
            } else {
                firebaseAuth
                    .startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener {
                        reference = FirebaseDatabase.getInstance().getReference("Users")
                        val user = FirebaseAuth.getInstance().currentUser
                        val userId = user?.uid
                        val signIn = "YAHOO"
                        if (userId != null) {
                            if (userId == user?.uid) {
                                reference.child(userId).child("signIn").setValue(signIn)
                            }
                        }
                        val intent = Intent(this, CoverPage::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Try Again ", Toast.LENGTH_SHORT).show()
                    }

            }

        }
    }



}





















