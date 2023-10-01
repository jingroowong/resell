package com.example.resell.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.resell.database.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailsRepository {

    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("OrderDetail")

    @Volatile private var INSTANCE : OrderDetailsRepository?= null

    fun getInstance() : OrderDetailsRepository {
        return INSTANCE ?: synchronized(this){

            val instance = OrderDetailsRepository()
            INSTANCE = instance
            instance
        }
    }


    fun loadOrderDetail(orderDetailList : MutableLiveData<List<OrderDetails>>){

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                try {

                    val _orderDetailList : List<OrderDetails> = snapshot.children.map { dataSnapshot ->

                        dataSnapshot.getValue(OrderDetails::class.java)!!

                    }
                    Log.d("FirebaseData", "Data History Detail Found")
                    orderDetailList.postValue(_orderDetailList)

                }catch (e : Exception){
                    Log.d("FirebaseData", "No data History Detail found")


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }
}