package com.example.resell.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.databinding.FragmentAdminLoginBinding
import com.google.firebase.auth.FirebaseAuth

class AdminLoginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentAdminLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()

        binding.adminLog.setOnClickListener {
            val email = binding.AdminloginEmail.text.toString()
            val pass = binding.AdminlogPass.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Handle successful login here, if needed
                        Toast.makeText(requireContext(), "Login Successfully", Toast.LENGTH_SHORT).show()
                        // Navigate to the desired fragment/activity after successful login
                        // For example:
                        // val intent = Intent(requireContext(), YourNextActivity::class.java)
                        // startActivity(intent)
                        // Navigate to Admin Main Page
                                this.findNavController().navigate(R.id.action_adminLoginFragment_to_adminViewProduct)
                    } else {
                        // Handle failed login here, if needed
                        Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Empty Fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
