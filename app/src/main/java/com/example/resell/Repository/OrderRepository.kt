import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.resell.database.Order
import com.example.resell.database.OrderDao
import com.google.firebase.database.*

class OrderRepository(private val orderDao: OrderDao) {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Order")

    // LiveData for observing order data
    // Function to fetch order data by ID
    suspend fun fetchOrderData(orderID: Int) {
        // Implement the logic to fetch order data here and update the LiveData
    }

    fun loadOrder(userList: MutableLiveData<List<Order>>) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _orderList: List<Order> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Order::class.java)!!
                    }
                    Log.d("FirebaseData", "Data Found")
                    userList.postValue(_orderList)
                } catch (e: Exception) {
                    Log.d("FirebaseData", "No data found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database query cancellation or errors
            }
        })
    }

    fun getOrderLiveData(): LiveData<Order> {
        TODO("Not yet implemented")
    }
}
