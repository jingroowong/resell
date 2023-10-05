package com.example.resell.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.MyAdapter
import com.example.resell.R
import com.example.resell.databinding.ActivityAdminCoverPageBinding
import com.example.resell.databinding.FragmentAdminCrudBinding
import com.example.resell.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminCrudFragment : Fragment() {

    private lateinit var binding: FragmentAdminCrudBinding
    private lateinit var reference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var usersArrayList: ArrayList<Users>
    private lateinit var recyclerView: RecyclerView
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminCrudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the NavController
        navController = findNavController()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.action_adminCrudFragment_to_adminViewProduct)
                }
                R.id.order -> {
                    navController.navigate(R.id.action_adminCrudFragment_to_chooseOrderToUpdate)
                }
                R.id.feedback -> {
                    navController.navigate(R.id.action_adminCrudFragment_to_adminFeedbackFragment2)
                }
                else -> {
                }
            }
            true
        }

        recyclerView = binding.recycleView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        usersArrayList = arrayListOf()

        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapShot in snapshot.children) {
                        val user = dataSnapShot.getValue(Users::class.java)
                        if (user != null) {
                            user.userId = dataSnapShot.key.toString()
                            Log.d("UserId", "UserId = ${user.userId}")
                        }

                        if (!usersArrayList.contains(user)) {
                            usersArrayList.add(user!!)
                        }
                    }
                    recyclerView.adapter = MyAdapter(usersArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}
