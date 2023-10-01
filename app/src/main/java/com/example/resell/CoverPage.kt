package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.loginpage.models.Users
import com.example.resell.databinding.CoverPageBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CoverPage :AppCompatActivity () {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: ImageView
    private lateinit var storageRef: StorageReference
    private lateinit var profilePicRef: StorageReference
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: CoverPageBinding
    private lateinit var firebase: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var users: Users
    private lateinit var account: GoogleSignInAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CoverPageBinding.inflate(layoutInflater)

       firebase = FirebaseAuth.getInstance()


        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val user = FirebaseAuth.getInstance().currentUser


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            reference = FirebaseDatabase.getInstance().getReference("Users")

            val userId = user?.uid
            if (userId != null) {
                    reference.child(userId).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            users = snapshot.getValue(Users::class.java)!!
                            if(users.username ==null) {
                                binding.newUsername.hint = "Update the username"
                            }else{
                                binding.newUsername.text = users.username
                            }
                            if (users.phone == null) {
                                binding.newPhone.hint = "Update the phone number"
                            } else {
                                binding.newPhone.text = users.phone
                            }
                            if (users.address == null) {
                                binding.newAddress.hint = "Update the Address"
                            }else{
                                binding.newAddress.text = users.address
                            }
                            binding.signIn.text = "Latest Login Method : " + users.signIn

                        }
                        override fun onCancelled(error: DatabaseError) {

                        }

                    })


 }
        imageView = findViewById(R.id.profile_picture_image_view)
        val uploadButton = findViewById<Button>(R.id.upload)

        // Get a reference to the Firebase Storage location where the profile picture will be stored
        storageRef = FirebaseStorage.getInstance().reference

        // Get a reference to the current user's profile picture in Firebase Storage
        val currentUser = FirebaseAuth.getInstance().currentUser

        profilePicRef = storageRef.child("images/$userId.jpg")

        // Check if the user has a profile picture in Firebase Storage
        profilePicRef.downloadUrl.addOnSuccessListener { uri ->
            // If the user has a profile picture, load it into the ImageView using Glide
            Glide.with(this).load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            // If the user doesn't have a profile picture, do nothing
            Log.d("ProfilePic", "Profile picture not available", exception)
        }
        binding.editProfile.setOnClickListener{
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)


        }

        binding.feedbackBtn.setOnClickListener {
            val intent = Intent(this, Feedback::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener{
            mGoogleSignInClient.signOut()
            firebase.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this,"LOG OUT SUCCESSFULLY",Toast.LENGTH_SHORT).show()
        }

    }
    private fun signOutAndStartSignInActivity() {



    }
}