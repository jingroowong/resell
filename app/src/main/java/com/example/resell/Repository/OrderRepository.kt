package com.example.firebaservrealdbk.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.resell.database.Order
import com.google.firebase.database.*

class OrderRepository {

    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Order")

    @Volatile private var INSTANCE : OrderRepository ?= null

    fun getInstance() : OrderRepository{
        return INSTANCE ?: synchronized(this){

            val instance = OrderRepository()
            INSTANCE = instance
            instance
        }


    }


    fun loadOrder(userList : MutableLiveData<List<Order>>){

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                try {

                    val _orderList : List<Order> = snapshot.children.map { dataSnapshot ->

                        dataSnapshot.getValue(Order::class.java)!!

                    }
                    Log.d("FirebaseData", "Data Found")
                    userList.postValue(_orderList)

                }catch (e : Exception){
                    Log.d("FirebaseData", "No data found")


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }

}