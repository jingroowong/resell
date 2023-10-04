package com.example.resell

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.databinding.ActivityAdminCoverPageBinding
import com.example.resell.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdminCrudUsers :AppCompatActivity () {


    private lateinit var binding: ActivityAdminCoverPageBinding
    private lateinit var reference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var usersArrayList: ArrayList<Users>
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCoverPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        usersArrayList = arrayListOf()

        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              if(snapshot.exists()){
                  for(dataSnapShot in snapshot.children){
                      val user = dataSnapShot.getValue(Users::class.java)
                      if (user != null) {
                          user.userId = dataSnapShot.key.toString()
                          Log.d("UserId","UserId = ${user.userId}")
                      }


                      if(!usersArrayList.contains(user)){
                          usersArrayList.add(user!!)
                      }
                  }
                  recyclerView.adapter = MyAdapter(usersArrayList)
              }
            }

            override fun onCancelled(error: DatabaseError) {
               Toast.makeText(this@AdminCrudUsers,error.toString(),Toast.LENGTH_SHORT).show()
            }

        })



    }


}

