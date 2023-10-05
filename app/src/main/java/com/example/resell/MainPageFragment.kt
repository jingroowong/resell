package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resell.databinding.FragmentMainPageBinding

class MainPageFragment : Fragment() {

    private lateinit var binding: FragmentMainPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.UserLogin.setOnClickListener {
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
            this.findNavController().navigate(R.id.action_mainPageFragment_to_loginMainFragment)
        }

        binding.AdminLogin.setOnClickListener {
//            val intent = Intent(requireContext(), AdminLogin::class.java)
//            startActivity(intent)
            this.findNavController().navigate(R.id.action_mainPageFragment_to_adminLoginFragment)
        }

        return view
    }
}
