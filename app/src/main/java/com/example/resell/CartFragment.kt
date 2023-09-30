import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.adapter.MyCartAdapter
import com.example.resell.database.Cart
import com.example.resell.database.Order
import com.example.resell.database.OrderDetail
import com.example.resell.database.Product
import com.example.resell.eventbus.UpdateCartEvent
import com.example.resell.listener.ICartLoadListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.example.resell.databinding.FragmentCartBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Boolean
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

class CartFragment : Fragment(), ICartLoadListener {
    private lateinit var binding: FragmentCartBinding
    private var cartLoadListener: ICartLoadListener? = null
    private lateinit var recyclerCart: RecyclerView
    //private lateinit var cartAdapter: MyCartAdapter

    // Add a variable to store the current orderID
    private var currentOrderID: Int? = 0
    // Add a variable to store the current userID
    private var currentUserID: Int? = 0

    private var cartModels: MutableList<Cart> = ArrayList()

    private var cartSum: Double? = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Retrieve userID from arguments
        currentUserID = arguments?.getInt("userID")

        // Inflate the layout for this fragment using View Binding
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartLoadListener = this

        checkExistingOrder()

        recyclerCart = binding.recyclerCart

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerCart.layoutManager = layoutManager
        recyclerCart.addItemDecoration(
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        )

        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("userID", currentUserID!!)
            val navController =
                this.findNavController().navigate(R.id.action_cartFragment_to_productFragment, bundle)
        }

        binding.checkOutButton.setOnClickListener {
            // Create an AlertDialog
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setTitle("Proceed to Payment ?")

            // Handle "Proceed to Payment" action
            dialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                // Handle the proceed to payment action here
                // Navigate to the payment gateway screen or perform payment processing
                val bundle = Bundle()
                bundle.putDouble("paymentAmount", cartSum!!)
                bundle.putInt("orderID",currentOrderID!!)
                val navController =
                    this.findNavController().navigate(R.id.action_cartFragment_to_paymentFragment, bundle)
                // Dismiss the dialog
                dialog.dismiss()
            }

            // Handle "Cancel" action
            dialogBuilder.setNegativeButton("Back") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }

            // Create and show the AlertDialog
            val dialog = dialogBuilder.create()
            dialog.show()
        }

        loadCartFromFirebase()



//        // Assign the adapter to the class-level variable
//        cartAdapter = adapter


    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onUpdateCartEvent(event: UpdateCartEvent) {
        loadCartFromFirebase()
    }

    private fun loadCartFromFirebase() {
        // Clear the cartModels list only once when initially loading the cart

            cartModels.clear()


        // Step 1: Get the orderID based on userID
        val orderRef = FirebaseDatabase.getInstance().getReference("Orders")
        val query = orderRef.orderByChild("userID").equalTo(currentUserID!!.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var orderID: Int? = null
                    Log.d("Debug", "Number of Orders: ${snapshot.childrenCount}")
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        if (order != null && !order.deal) {
                            // An uncompleted order exists, use its orderID
                            orderID = order.orderID
                            Log.d("Debug", "Order ID: $orderID")
                            break
                        }
                    }

                    if (orderID != null) {
                        // Step 2: Fetch orderDetails using the obtained orderID
                        val orderDetailsRef =
                            FirebaseDatabase.getInstance().getReference("OrderDetail")
                        val orderDetailQuery =
                            orderDetailsRef.orderByChild("orderID").equalTo(orderID.toDouble())
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(orderDetailsSnapshot: DataSnapshot) {
                                        for (orderDetailData in orderDetailsSnapshot.children) {
                                            val orderDetail =
                                                orderDetailData.getValue(OrderDetail::class.java)

                                            if (orderDetail != null) {
                                                val productID = orderDetail.productID.toString()

                                                // Retrieve product information from Product table
                                                val productRef = FirebaseDatabase.getInstance()
                                                    .getReference("Products")
                                                    .child(productID)
                                                    .addListenerForSingleValueEvent(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(productSnapshot: DataSnapshot) {
                                                            val product =
                                                                productSnapshot.getValue(Product::class.java)
                                                            if (product != null && product.productAvailability == Boolean.TRUE) {
                                                                // Convert OrderDetail to Cart
                                                                val cartModel = Cart()
                                                                cartModel.productID =
                                                                    product.productID
                                                                cartModel.orderID = orderID
                                                                cartModel.productName =
                                                                    product.productName
                                                                cartModel.productImage =
                                                                    product.productImage
                                                                cartModel.productPrice =
                                                                    product.productPrice

                                                                cartModels.add(cartModel)

                                                                // Check if we've collected all the cart items
                                                                if (cartModels.size == orderDetailsSnapshot.childrenCount.toInt()) {
                                                                    cartLoadListener?.onLoadCartSuccess(cartModels)
                                                                }

                                                            }
                                                        }

                                                        override fun onCancelled(productError: DatabaseError) {
                                                            cartLoadListener?.onLoadCartFailed(
                                                                productError.message
                                                            )
                                                        }
                                                    })
                                            }
                                        }
                                    }

                                    override fun onCancelled(orderDetailsError: DatabaseError) {
                                        cartLoadListener?.onLoadCartFailed(orderDetailsError.message)
                                    }
                                })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener?.onLoadCartFailed(error.message)
                }
            })
    }

    private fun checkExistingOrder() {
        val orderRef = FirebaseDatabase.getInstance().getReference("Orders")
        orderRef.orderByChild("userID").equalTo(currentUserID!!.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        if (order != null && order.deal==false) {
                            // An uncompleted order exists, use its orderID
                            currentOrderID = order.orderID
                            return
                        }
                    }
                    // No uncompleted order found, generate a new order
                    generateNewOrder(currentUserID!!) // Implement this function
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
    }

    private fun generateNewOrder(currentUserID: Int) {
        val orderRef = FirebaseDatabase.getInstance().getReference("Orders")

        // Get the current timestamp as a unique identifier (assuming it's an int)
        val newOrderID = getCurrentTimestampAsInt()

        // Create a new Order with an Int orderID
        val newOrder = Order(
            orderID = newOrderID,
            orderDate = getCurrentTimestampAsString(),
            orderAmount = 0.0, // Initial amount
            deal = false,
            orderStatus = "In Progress",
            userID = currentUserID,
            paymentID = 0 // Replace with the actual payment ID
        )

        // Set the value with the new order ID
        orderRef.child(newOrderID.toString()).setValue(newOrder)

        // Update the currentOrderID
        currentOrderID = newOrderID
    }

    private fun getCurrentTimestampAsInt(): Int {
        val currentTimeMillis = System.currentTimeMillis()
        // Depending on your requirements, you can manipulate the timestamp further
        // to ensure uniqueness if needed.
        return currentTimeMillis.toInt()
    }


    private fun getCurrentTimestampAsString(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(date)
    }


    private fun init() {

    }

    override fun onLoadCartSuccess(cartList: List<Cart>) {

       var sum = 0.0
        for (cartModel in cartList) {
            sum += cartModel.productPrice ?: 0.0
        }
        val decimalFormat = DecimalFormat("RM #,##0.00")
        binding.txtTotal.text = decimalFormat.format(sum)

        val adapter = MyCartAdapter(requireContext(), cartList,this)
        recyclerCart.adapter = adapter

        cartSum = sum
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(requireView(), message ?: "An error occurred", Snackbar.LENGTH_LONG).show()
    }

    override fun onCartItemDeleted(position: Int) {
        val adapter = MyCartAdapter(requireContext(), cartModels,this)
        if (position >= 0 && position < cartModels.size) {
            // Remove the item from cartModelList if it's a valid position
            cartModels.removeAt(position)

            // Notify the adapter of the removal
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, cartModels.size)

            // Check if the item being removed is the last item
            if (cartModels.isEmpty()) {
                adapter.notifyDataSetChanged()
            }
        }
        var sum = 0.0
        for (cartModel in cartModels) {
            sum += cartModel.productPrice ?: 0.0
        }
        val decimalFormat = DecimalFormat("RM #,##0.00")
        binding.txtTotal.text = decimalFormat.format(sum)

        recyclerCart.adapter = adapter
    }
}
