package com.example.resell

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.loginpage.models.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.soundcloud.android.crop.Crop
import java.io.File


class EditProfile : Activity() {


    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: ImageView
    private lateinit var storageRef: StorageReference
    private lateinit var profilePicRef: StorageReference
    private lateinit var reference: DatabaseReference
    private lateinit var users: Users
    private lateinit var firebase: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        reference = FirebaseDatabase.getInstance().getReference("Users")
        val textView = findViewById<TextView>(R.id.name)
        val user = FirebaseAuth.getInstance().currentUser
        val serId = user?.uid


        if (serId != null) {

            reference.child(serId).addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    users = snapshot.getValue(Users::class.java)!!
                    textView.text = users.username

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
        val userId = currentUser?.uid
        profilePicRef = storageRef.child("images/$userId.jpg")

        // Check if the user has a profile picture in Firebase Storage
        profilePicRef.downloadUrl.addOnSuccessListener { uri ->
            // If the user has a profile picture, load it into the ImageView using Glide
            Glide.with(this).load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            // If the user doesn't have a profile picture, do nothing
            Log.d("ProfilePic", "Profile picture not available", exception)
        }

        // Set up the button to open the image picker
        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


        val deleteButton = findViewById<Button>(R.id.remove)
        deleteButton.setOnClickListener {
            if (userId != null) {

                storageRef = FirebaseStorage.getInstance().getReference("images/$userId.jpg")
                storageRef.delete().addOnSuccessListener {
                    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId")
                    val updates = HashMap<String, Any?>()
                    updates["profile_pic_url"] = null
                    imageView.setImageResource(R.drawable.man_pro)
                    Toast.makeText(this, "User profile removed successfully", Toast.LENGTH_SHORT)
                        .show()

                    databaseRef.updateChildren(updates).addOnSuccessListener {
                        // User profile updated successfully


                    }.addOnFailureListener {
                        // Failed to update user profile
                    }
                }.addOnFailureListener {
                    // Failed to delete image file

                    Toast.makeText(this, "Failed to delete image file", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val back = findViewById<Button>(R.id.backBtn)
        back.setOnClickListener {
            val intent = Intent(this, CoverPage::class.java)
            startActivity(intent)
            finish()
        }

        val editBtn = findViewById<Button>(R.id.updateBtn)
        editBtn.setOnClickListener {
            val address = findViewById<EditText>(R.id.newAddress).text.toString()
            val username = findViewById<EditText>(R.id.newUsername).text.toString()
            val phone = findViewById<EditText>(R.id.newPhone).text.toString()



            reference = FirebaseDatabase.getInstance().getReference("Users")
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid
            if (username.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                if (userId != null) {
                    if (userId == user?.uid) {

                        reference.child(userId).child("username").setValue(username)
                        reference.child(userId).child("address").setValue(address)
                        reference.child(userId).child("phone").setValue(phone)

                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, CoverPage::class.java)
                        startActivity(intent)
                        finish()


                    } else {
                        Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Empty Field is not allowed",Toast.LENGTH_SHORT).show()
            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the URI of the selected image
            val imageUri = data.data



            // Start the crop activity
            Crop.of(imageUri, Uri.fromFile(File(this.externalCacheDir, "cropped_image.jpg")))
                .asSquare().start(this)



            Handler().postDelayed({
                // Upload the cropped image to Firebase Storage
                val file = File(this.externalCacheDir, "cropped_image.jpg")

                val uploadTask = profilePicRef.putFile(Uri.fromFile(file))

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation profilePicRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Glide.with(this).load(downloadUri).into(imageView)
                    } else {
                        // Handle errors here
                        Log.d("UploadError", task.exception.toString())
                    }
                }


            }, 5000) // 5000 milliseconds = 5 seconds

        }
    }

    private fun loadProfilePicture() {
        // Get a reference to the current user's profile picture in Firebase Storage
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        profilePicRef = storageRef.child("images/$userId.jpg")

        // Check if the user has a profile picture in Firebase Storage
        profilePicRef.downloadUrl.addOnSuccessListener { uri ->
            // If the user has a profile picture, load it into the ImageView using Glide
            Glide.with(this).load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            // If the user doesn't have a profile picture, set a default image
            imageView.setImageResource(R.drawable.man_pro)
            Log.d("ProfilePic", "Profile picture not available", exception)
        }
    }








}

