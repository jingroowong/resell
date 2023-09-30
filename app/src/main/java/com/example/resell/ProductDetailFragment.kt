import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resell.R
import com.example.resell.database.Cart
import com.example.resell.database.Order
import com.example.resell.database.OrderDetail
import com.example.resell.database.Product
import com.example.resell.databinding.FragmentProductDetailBinding
import com.example.resell.listener.ICartLoadListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import com.google.android.material.snackbar.Snackbar
import com.nex3z.notificationbadge.NotificationBadge
import java.lang.Boolean.TRUE
import java.text.SimpleDateFormat
import java.util.Date

class ProductDetailFragment : Fragment(), ICartLoadListener {

    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var cartLoadListener: ICartLoadListener
    private lateinit var badge: NotificationBadge

    // Add a variable to store the current orderID
    private var currentOrderID: Int? = 0

    // Add a variable to store the current userID
    private var currentUserID: Int? = 0
    private var cartModels: MutableList<Cart> = ArrayList()
    companion object {
        private const val ARG_PRODUCT = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PRODUCT, product)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve userID from arguments
        currentUserID = arguments?.getInt("userID")

        // Inflate the layout for this fragment using View Binding
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartLoadListener = this

        checkExistingOrder()
        badge = binding.badge

        binding.btnCart.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("userID", currentUserID!!)

            //Navigate to Cart
            this.findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment,bundle)
        }

        binding.btnBack.setOnClickListener {
            // Use the FragmentManager to pop the current fragment from the back stack
            fragmentManager?.popBackStack()
        }

        // Get the product data from arguments
        val product = arguments?.getParcelable(ARG_PRODUCT) as Product?

        // Update UI with product details
        product?.let {
            val nameTextView = view.findViewById<TextView>(R.id.name_label)
            val priceTextView = view.findViewById<TextView>(R.id.price_label)
            val conditionTextView = view.findViewById<TextView>(R.id.condition_label2)
            val descTextView = view.findViewById<TextView>(R.id.desc_label)
            val addToCartButton = view.findViewById<Button>(R.id.add_to_cart_button)

            nameTextView.text = it.productName
            priceTextView.text = getString(R.string.price_format, it.productPrice)
            conditionTextView.text = it.productCondition
            descTextView.text = it.productDesc

            if (it.productCondition == "Good") {
                conditionTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorGood
                    )
                )
            } else if (it.productCondition == "Moderate") {
                conditionTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorModerate
                    )
                )
            }

            addToCartButton.setOnClickListener {
                addToCart(product)
            }

            val productImageView = view.findViewById<ImageView>(R.id.product_image)
            Glide.with(requireContext())
                .load(it.productImage)
                .into(productImageView)
        }
        countCartFromFirebase()
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

    private fun checkExistingOrder() {
        val orderRef = FirebaseDatabase.getInstance().getReference("Orders")
        orderRef.orderByChild("userID").equalTo(currentUserID!!.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        if (order != null && !order.deal) {
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


    private fun countCartFromFirebase() {
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
                        break
                    }
                }

                if (orderID != null) {
                    // Step 2: Fetch orderDetails using the obtained orderID
                    val orderDetailsRef = FirebaseDatabase.getInstance().getReference("OrderDetail")
                      val orderDetailQuery = orderDetailsRef.orderByChild("orderID").equalTo(orderID.toDouble())
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
                                        .child(productID).addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(productSnapshot: DataSnapshot) {
                                            val product =
                                                productSnapshot.getValue(Product::class.java)
                                            if (product != null && product.productAvailability == TRUE) {
                                                // Convert OrderDetail to Cart
                                                val cartModel = Cart()
                                                cartModel.productID = product.productID
                                                cartModel.orderID = orderID
                                                cartModel.productName = product.productName
                                                cartModel.productImage = product.productImage
                                                cartModel.productPrice = product.productPrice

                                                cartModels.add(cartModel)
                                                cartLoadListener?.onLoadCartSuccess(cartModels)


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
                }             }

            override fun onCancelled(error: DatabaseError) {
                cartLoadListener?.onLoadCartFailed(error.message)
            }
        })
    }

    private fun addToCart(product: Product) {
        val userCart = FirebaseDatabase.getInstance().getReference("OrderDetail")

        userCart.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var itemExistsInCart = false

                for (orderDetailSnapshot in dataSnapshot.children) {
                    val orderDetail = orderDetailSnapshot.getValue(OrderDetail::class.java)

                    // Check if the retrieved orderDetail has the matching productID and orderID
                    if (orderDetail != null &&
                        orderDetail.productID == product.productID &&
                        orderDetail.orderID == currentOrderID
                    ) {
                        // If the item already exists in the cart, set the flag and break the loop.
                        itemExistsInCart = true
                        break
                    }
                }

                if (itemExistsInCart) {
                    // If the item already exists in the cart, you can show a message or take appropriate action.
                    cartLoadListener.onLoadCartFailed("Item already added to cart")
                    Log.d("Debug", "Item already exists in cart")
                } else {

                    // If the item is not in the cart, add it as an OrderDetail.
                    val orderDetail = OrderDetail(
                        orderID = currentOrderID!!, // Use the current order ID
                        productID = product.productID!! // Convert the product key to an Int
                    )

                    // Generate a new unique key for the OrderDetail
                    val newOrderDetailKey = userCart.push().key

                    // Store the OrderDetail in Firebase with the generated key
                    userCart.child(newOrderDetailKey ?: "").setValue(orderDetail)
                        .addOnSuccessListener {
                            cartLoadListener?.onLoadCartFailed("Item added to cart")
                            countCartFromFirebase()
                            Log.d("Debug", "Item added to cart")
                        }
                        .addOnFailureListener { e ->
                            onLoadCartFailed(e.message)
                            Log.e("Error", "Failed to add item to cart: ${e.message}")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                cartLoadListener?.onLoadCartFailed(error.message)
                Log.e("Error", "Database error: ${error.message}")
            }
        })
    }



    override fun onLoadCartSuccess(cartList: List<Cart>) {
        var cartSum = cartList.size
        badge.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(requireView(), message ?: "An error occurred", Snackbar.LENGTH_SHORT).show()
    }

    override fun onCartItemDeleted(position: Int) {
        TODO("Not yet implemented")
    }


}
