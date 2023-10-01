package com.example.resell

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resell.database.UserViewModel
import com.example.resell.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userID = 101
        val loginBtn = binding.button

        loginBtn.setOnClickListener {
            val userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
            userViewModel.userID = userID

            // Navigate to Main Page
            this.findNavController().navigate(R.id.action_loginFragment_to_productFragment)

        }
    }

}