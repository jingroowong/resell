package com.example.resell

import ProductFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resell.database.UserViewModel
import com.example.resell.databinding.FragmentCoverPageBinding
import com.example.resell.models.Users
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

class CoverPageFragment : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: ImageView
    private lateinit var storageRef: StorageReference
    private lateinit var profilePicRef: StorageReference
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentCoverPageBinding
    private lateinit var firebase: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var users: Users
    private lateinit var account: GoogleSignInAccount

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the NavController
        binding = FragmentCoverPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebase = FirebaseAuth.getInstance()
        // Initialize NavController
        navController = findNavController()
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cart -> {
                    navController.navigate(R.id.action_coverPageFragment_to_cartFragment)
                }
                R.id.history -> {
                    navController.navigate(R.id.action_coverPageFragment_to_orderHistory)
                }
                R.id.home -> {
                    navController.navigate(R.id.action_coverPageFragment_to_productFragment)
                }
                else -> {
                }
            }
            true
        }

        // Initialize Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Get the current user
        val user = FirebaseAuth.getInstance().currentUser

        // Initialize Google Sign-In client
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // Initialize Firebase references
        reference = FirebaseDatabase.getInstance().getReference("Users")
        val userId = user?.uid

        if (userId != null) {
            val userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
            userViewModel.userID = userId
            reference.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    users = snapshot.getValue(Users::class.java)!!
                    if (users.username == null) {
                        binding.newUsername.hint = "Update the username"
                    } else {
                        binding.newUsername.text = users.username
                    }
                    if (users.phone == null) {
                        binding.newPhone.hint = "Update the phone number"
                    } else {
                        binding.newPhone.text = users.phone
                    }
                    if (users.address == null) {
                        binding.newAddress.hint = "Update the Address"
                    } else {
                        binding.newAddress.text = users.address
                    }
                    binding.signIn.text = "Latest Login Method : " + users.signIn
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                }
            })
        }

        imageView = binding.profilePictureImageView

        // Get a reference to the Firebase Storage location where the profile picture will be stored
        storageRef = FirebaseStorage.getInstance().reference

        // Get a reference to the current user's profile picture in Firebase Storage
        profilePicRef = storageRef.child("images/$userId.jpg")

        // Check if the user has a profile picture in Firebase Storage
        profilePicRef.downloadUrl.addOnSuccessListener { uri ->
            // If the user has a profile picture, load it into the ImageView using Glide
            Glide.with(this@CoverPageFragment).load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            // If the user doesn't have a profile picture, do nothing
            Log.d("ProfilePic", "Profile picture not available", exception)
        }

        binding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)

        }

        binding.feedbackBtn.setOnClickListener {
            val intent = Intent(requireContext(), Feedback::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {

         val signout = FirebaseAuth.getInstance()
            signout.signOut()


//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
            this.findNavController().navigate(R.id.action_coverPageFragment_to_loginMainFragment)
            Toast.makeText(requireContext(), "LOG OUT SUCCESSFULLY", Toast.LENGTH_SHORT).show()
        }


    }
}
