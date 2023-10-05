package com.example.resell


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.admin.MyAdminFeedbackAdapter
import com.example.resell.databinding.FragmentAdminFeedbackBinding
import com.example.resell.models.AdminFeedback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminFeedbackFragment : Fragment() {

    private lateinit var binding: FragmentAdminFeedbackBinding
    private lateinit var feedbackArrayList: ArrayList<AdminFeedback>
    private lateinit var recyclerView: RecyclerView
    private lateinit var reference: DatabaseReference
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the NavController
        navController = findNavController()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.action_adminFeedbackFragment2_to_adminViewProduct)
                }
                R.id.order -> {
                    navController.navigate(R.id.action_adminFeedbackFragment2_to_chooseOrderToUpdate)
                }
                R.id.user -> {
                    navController.navigate(R.id.action_adminFeedbackFragment2_to_adminCrudFragment)
                }
                else -> {
                }
            }
            true
        }

        recyclerView = binding.feedbackRecycle
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        feedbackArrayList = arrayListOf()

        reference = FirebaseDatabase.getInstance().getReference("Feedback")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(dataSnapShot in snapshot.children){
                        val feedback = dataSnapShot.getValue(AdminFeedback::class.java)
                        if (feedback != null) {
                            feedback?.orderId = dataSnapShot.key?.toInt()?.toLong()
                            Log.d("UserId","UserId = ${feedback.orderId}")
                        }


                        if(!feedbackArrayList.contains(feedback)){
                            feedbackArrayList.add(feedback!!)
                        }
                    }
                    recyclerView.adapter = MyAdminFeedbackAdapter(feedbackArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }
}

