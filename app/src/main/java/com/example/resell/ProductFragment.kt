import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.adapter.MyProductAdapter
import com.example.resell.database.Cart
import com.example.resell.database.Product
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

class ProductFragment : Fragment(), IProductLoadListener, ICartLoadListener {
    private lateinit var binding: FragmentProductBinding
    private lateinit var productLoadListener: IProductLoadListener
    private lateinit var cartLoadListener: ICartLoadListener
    private lateinit var badge: NotificationBadge
    private lateinit var recyclerProduct: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment using View Binding
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerProduct = binding.recyclerProduct
        badge = binding.badge

        init()
        loadProductFromFirebase()
        countCartFromFirebase()
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
        val cartModels: MutableList<Cart> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID") // Replace with your user ID
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children) {
                        val cartModel = cartSnapshot.getValue(Cart::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
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
                            productModel!!.key = productSnapshot.key
                            productModels.add(productModel)
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

    private fun init() {
        productLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        recyclerProduct.addItemDecoration(SpaceItemDecoration())
        recyclerProduct.layoutManager = gridLayoutManager

        binding.btnCart.setOnClickListener {
            //Navigate to Cart
            val navController = this.findNavController().navigate(R.id.action_productFragment_to_cartFragment)
        }
    }

    override fun onProductLoadSuccess(productModelList: List<Product>?) {
        val adapter = MyProductAdapter(requireContext(), productModelList!!,findNavController())
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




}
