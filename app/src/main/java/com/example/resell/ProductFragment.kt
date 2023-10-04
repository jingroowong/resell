
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.adapter.MyProductAdapter
import com.example.resell.database.AppDatabase
import com.example.resell.database.Cart
import com.example.resell.database.Order
import com.example.resell.database.OrderDetails
import com.example.resell.database.OrderViewModel
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import com.example.resell.database.UserViewModel
import com.example.resell.eventbus.UpdateCartEvent
import com.example.resell.listener.ICartLoadListener
import com.example.resell.listener.IProductLoadListener
import com.example.resell.utills.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.nex3z.notificationbadge.NotificationBadge
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.example.resell.databinding.FragmentProductBinding
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.text.SimpleDateFormat
import java.util.Date
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.navigation.NavController
import com.example.resell.CoverPage
import com.example.resell.MainActivity


class ProductFragment : Fragment(), IProductLoadListener, ICartLoadListener {
    private lateinit var binding: FragmentProductBinding
    private lateinit var navController: NavController

    private lateinit var productLoadListener: IProductLoadListener
    private lateinit var cartLoadListener: ICartLoadListener
    private lateinit var badge: NotificationBadge
    private lateinit var recyclerProduct: RecyclerView

    private var originalProductList: List<Product> = ArrayList()
    private var filteredProductList: List<Product> = ArrayList()

    // Add a variable to store the current userID
    private var currentUserID: Int? = 0
    // Declare the OrderViewModel property
    private lateinit var orderViewModel: OrderViewModel

    private var cartModels: MutableList<Cart> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        // Retrieve userID
        currentUserID = userViewModel.userID

        Log.d("Debug", "User ID in Product Fragment ${currentUserID}")
        // Inflate the layout for this fragment using View Binding
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the NavController
        navController = findNavController()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cart -> {
                    navController.navigate(R.id.action_productFragment_to_cartFragment)
                }
                R.id.history -> {
                    navController.navigate(R.id.action_productFragment_to_orderHistory)
                }
                R.id.profile -> {
                    val intent = Intent(requireContext(), CoverPage::class.java)
                    startActivity(intent)
                }
                else -> {
                }
            }
            true
        }

        // Initialize the OrderViewModel
        orderViewModel = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)

        val productViewModel = (requireActivity() as MainActivity).productViewModel

        if (isInternetAvailable(requireContext())) {
            productViewModel.clearAll()

        }
        readFirebaseProduct(productViewModel)
        productViewModel.getAllProducts().observe(viewLifecycleOwner, { products ->
                if (products != null) {
                    originalProductList = products ?: emptyList()
                    filteredProductList = originalProductList
                    val adapter =
                        MyProductAdapter(requireContext(), filteredProductList, findNavController())
                    recyclerProduct.adapter = adapter
                }
            })





        productLoadListener = this
        cartLoadListener = this

        checkExistingOrder()

        recyclerProduct = binding.recyclerProduct
        badge = binding.badge


        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        recyclerProduct.addItemDecoration(SpaceItemDecoration())
        recyclerProduct.layoutManager = gridLayoutManager

        binding.btnCart.setOnClickListener {
            //Navigate to Cart
            findNavController().navigate(R.id.action_productFragment_to_cartFragment)
        }


        loadProductFromFirebase()

        var searchView = binding.searchView
        binding.searchButton.setOnClickListener {
            // Toggle the visibility of the SearchView
            if (searchView.visibility == View.VISIBLE) {
                searchView.visibility = View.GONE
                binding.topAppBar.title=""
            } else {
                searchView.visibility = View.VISIBLE
                binding.topAppBar.title="Item List"
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission if needed
                if (!query.isNullOrEmpty()) {
                    // User submitted a non-empty query
                    filterProducts(query)
                }
                return true // Return true to indicate that you've handled the query submission
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // This method is called when the text in the SearchView changes,
                // you can use it for real-time filtering as the user types
                if (!newText.isNullOrEmpty()) {
                    filterProducts(newText)
                } else {
                    // If the query is empty, show the full product list
                    filterProducts("") // Pass an empty query to show all products
                }
                return true
            }
        })
        countCartFromFirebase()

    }

    fun readFirebaseProduct(viewModel: ProductViewModel) {
        FirebaseDatabase.getInstance()
            .getReference("Products")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (productSnapshot in snapshot.children) {
                            val productModel = productSnapshot.getValue(Product::class.java)
                            if (productModel!!.productAvailability == TRUE) {
                                // Convert the Firebase data to a Product object
                                val productData = productSnapshot.getValue(Product::class.java)

                                if (productData != null) {
                                    viewModel.getProductById(productData.productID)
                                        .observe(viewLifecycleOwner, { product ->
                                            if (product == null) {
                                                viewModel.insertProduct(productData)
                                            }

                                        })
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors here
                }
            })


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
        countCartFromFirebase()
    }

    private fun countCartFromFirebase() {
        cartModels.clear()

        // Get the order ID from the ViewModel
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
                                    if (product != null && product.productAvailability == TRUE) {
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
            paymentID = 0
        )

        // Set the value with the new order ID
        orderRef.child(newOrderID.toString()).setValue(newOrder)

        // Update the currentOrderID

        orderViewModel.orderID = newOrderID
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
                        if (order != null && order.deal == FALSE) {
                            // An uncompleted order exists, use its orderID

                            orderViewModel.orderID = order.orderID
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

    private fun loadProductFromFirebase() {
        val productModels: MutableList<Product> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Products")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (productSnapshot in snapshot.children) {
                            val productModel = productSnapshot.getValue(Product::class.java)
                            if (productModel!!.productAvailability == TRUE) {
                                productModels.add(productModel!!)
                            }
                        }
                        productLoadListener.onProductLoadSuccess(productModels)
                        Log.d("FirebaseData", "Data retrieved successfully")
                    } else {
                        productLoadListener.onProductLoadFailed("Product items not exist")
                        Log.d("FirebaseData", "No data found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    productLoadListener.onProductLoadFailed(error.message)
                }
            })
    }

    override fun onProductLoadSuccess(productModelList: List<Product>?) {
        originalProductList = productModelList ?: emptyList()
        filteredProductList = originalProductList
        val adapter = MyProductAdapter(requireContext(), filteredProductList, findNavController())
        recyclerProduct.adapter = adapter
    }

    private fun filterProducts(query: String?) {
        if (query.isNullOrEmpty()) {
            // If the query is empty, show the original product list
            filteredProductList = originalProductList
        } else {
            // Filter the product list based on the query
            filteredProductList = originalProductList.filter { product ->
                product.productName!!.contains(query, ignoreCase = true)
            }
        }

        // Update the RecyclerView with the filtered list
        val adapter = MyProductAdapter(requireContext(), filteredProductList, findNavController())
        recyclerProduct.adapter = adapter
    }

    override fun onProductLoadFailed(message: String?) {
        Snackbar.make(binding.productLayout, message!!, Snackbar.LENGTH_LONG).show()
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

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

}
