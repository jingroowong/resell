import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.adapter.MyCartAdapter
import com.example.resell.database.Cart
import com.example.resell.database.OrderDetails
import com.example.resell.database.OrderViewModel
import com.example.resell.database.Product
import com.example.resell.database.UserViewModel
import com.example.resell.databinding.FragmentCartBinding
import com.example.resell.eventbus.UpdateCartEvent
import com.example.resell.listener.ICartLoadListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Boolean
import java.text.DecimalFormat
import kotlin.Double
import kotlin.Int
import kotlin.String

class CartFragment : Fragment(), ICartLoadListener {
    private lateinit var binding: FragmentCartBinding
    private var cartLoadListener: ICartLoadListener? = null
    private lateinit var recyclerCart: RecyclerView
    private lateinit var cartAdapter: MyCartAdapter

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
        val userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        // Retrieve userID
        currentUserID = userViewModel.userID
        Log.d("Debug", "User ID in Cart Fragment ${currentUserID}")
        val orderViewModel = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        currentOrderID = orderViewModel.orderID
        Log.d("Debug", "Order ID in Cart Fragment ${currentOrderID}")
        // Inflate the layout for this fragment using View Binding
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerCart = binding.recyclerCart
        // Assign the adapter to the class-level variable

        //checkExistingOrder()
        init()


        val adapter = MyCartAdapter(requireContext(), cartModels, this)
        recyclerCart.adapter = adapter

        // Assign the adapter to the class-level variable
        cartAdapter = adapter

        loadCartFromFirebase()
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
        cartModels.clear()

        // Get the order ID from the ViewModel
        val orderViewModel = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        val orderID = orderViewModel.orderID

        if (orderID != null) {
            // Fetch orderDetails using the obtained orderID
            val orderDetailsRef = FirebaseDatabase.getInstance().getReference("OrderDetail")
            val orderDetailQuery = orderDetailsRef.orderByChild("orderID").equalTo(orderID.toDouble())

            orderDetailQuery.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(orderDetailsSnapshot: DataSnapshot) {
                    Log.d(
                        "Debug",
                        "Number of Order Detail in Product Fragment: ${orderDetailsSnapshot.childrenCount}"
                    )
                    for (orderDetailData in orderDetailsSnapshot.children) {
                        val orderDetail = orderDetailData.getValue(OrderDetails::class.java)
                        if (orderDetail != null) {
                            val productID = orderDetail.productID.toString()

                            // Retrieve product information from Product table
                            val productRef = FirebaseDatabase.getInstance()
                                .getReference("Products")
                                .child(productID)

                            productRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(productSnapshot: DataSnapshot) {
                                    val product = productSnapshot.getValue(Product::class.java)
                                    if (product != null && product.productAvailability == Boolean.TRUE) {
                                        // Convert OrderDetail to Cart
                                        val cartModel = Cart()
                                        cartModel.productID = product.productID
                                        cartModel.orderID = orderID
                                        cartModel.productName = product.productName
                                        cartModel.productImage = product.productImage
                                        cartModel.productPrice = product.productPrice

                                        cartModels.add(cartModel)

                                        // Check if we've collected all the cart items
                                        if (cartModels.size == orderDetailsSnapshot.childrenCount.toInt()) {
                                            cartLoadListener?.onLoadCartSuccess(cartModels)
                                        }
                                    }
                                }

                                override fun onCancelled(productError: DatabaseError) {
                                    cartLoadListener?.onLoadCartFailed(productError.message)
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

    private fun init() {
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerCart.layoutManager = layoutManager
        recyclerCart.addItemDecoration(
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        )

        binding.btnBack.setOnClickListener {
         findNavController().navigate(R.id.action_cartFragment_to_productFragment)
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
                findNavController().navigate(R.id.action_cartFragment_to_paymentFragment, bundle)
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
        if (position >= 0 && position < cartModels.size) {
            // Remove the item from cartModelList if it's a valid position
            cartModels.removeAt(position)

            // Notify the adapter of the removal
            cartAdapter.notifyItemRemoved(position)
            cartAdapter.notifyItemRangeChanged(position, cartModels.size)

            // Check if the item being removed is the last item
            if (cartModels.isEmpty()) {
                cartAdapter.notifyDataSetChanged()
            }
        }
        var sum = 0.0
        for (cartModel in cartModels) {
            sum += cartModel.productPrice ?: 0.0
        }
        val decimalFormat = DecimalFormat("RM #,##0.00")
        binding.txtTotal.text = decimalFormat.format(sum)
        val adapter = MyCartAdapter(requireContext(), cartModels,this)
        recyclerCart.adapter = adapter
    }
}
