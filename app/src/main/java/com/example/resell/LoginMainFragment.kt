package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.databinding.FragmentLoginBinding
import com.example.resell.databinding.FragmentLoginMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginMainFragment : Fragment() {

    private lateinit var binding: FragmentLoginMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginMainBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()

        binding.googleBtn.setOnClickListener {
            val intent = Intent(requireContext(), GoogleActivity::class.java)
            startActivity(intent)
        }

        binding.logBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val pass = binding.logPass.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
//                        val intent = Intent(requireContext(), CoverPage::class.java)
//                        startActivity(intent)
                        this.findNavController().navigate(R.id.action_loginMainFragment_to_coverPageFragment)
                        Toast.makeText(requireContext(), "Login Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Empty Fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.regLink.setOnClickListener {
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPass.setOnClickListener {
            val intent = Intent(requireContext(), ForgotPassword::class.java)
            startActivity(intent)
        }

        binding.btnTwitter.setOnClickListener {
            val intent = Intent(requireContext(), TwitterActivity::class.java)
            startActivity(intent)
        }

        binding.btnYahoo.setOnClickListener {
            val provider = OAuthProvider.newBuilder("yahoo.com")
            provider.addCustomParameter("prompt", "login")
            provider.addCustomParameter("language", "en")

            val pendingResultTask = firebaseAuth.pendingAuthResult
            if (pendingResultTask != null) {
                pendingResultTask
                    .addOnSuccessListener {
//                        val intent = Intent(requireContext(), CoverPage::class.java)
//                        startActivity(intent)
                        this.findNavController().navigate(R.id.action_loginMainFragment_to_coverPageFragment)
                        Toast.makeText(requireContext(), "Login Successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_SHORT).show()
                    }
            } else {
                firebaseAuth
                    .startActivityForSignInWithProvider(requireActivity(), provider.build())
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
//                        val intent = Intent(requireContext(), CoverPage::class.java)
//                        startActivity(intent)
                        this.findNavController().navigate(R.id.action_loginMainFragment_to_coverPageFragment)
                        Toast.makeText(requireContext(), "Login Successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_SHORT).show()
                    }
            }

        }

        return view
    }
}
