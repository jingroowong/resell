import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.resell.R
import com.example.resell.database.Cart
import com.example.resell.database.Product
import com.example.resell.eventbus.UpdateCartEvent
import com.example.resell.listener.ICartLoadListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import com.google.android.material.snackbar.Snackbar
import com.nex3z.notificationbadge.NotificationBadge

class ProductDetailFragment : Fragment(), ICartLoadListener  {
    private lateinit var cartLoadListener: ICartLoadListener
    private lateinit var badge: NotificationBadge
    private lateinit var btnBack: ImageView
    private lateinit var btnCart: FrameLayout
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
        // Inflate the layout for this fragment using View Binding

       return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartLoadListener = this
        // Get the product data from arguments
        val product = arguments?.getParcelable(ARG_PRODUCT) as Product?

        // Update UI with product details
        product?.let {
            val nameTextView = view.findViewById<TextView>(R.id.name_label)
            val priceTextView = view.findViewById<TextView>(R.id.price_label)
            val conditionTextView = view.findViewById<TextView>(R.id.condition_label2)
            val descTextView = view.findViewById<TextView>(R.id.desc_label)
            val addToCartButton = view.findViewById<Button>(R.id.add_to_cart_button)

            badge =view.findViewById(R.id.badge)
            btnCart =view.findViewById<FrameLayout>(R.id.btnCart)
btnBack = view.findViewById(R.id.btnBack)
                nameTextView.text = it.productName
            priceTextView.text = getString(R.string.price_format, it.productPrice)
            conditionTextView.text = it.productCondition
            descTextView.text = it.productDesc

            if (it.productCondition == "Good") {
                conditionTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGood))
            } else if (it.productCondition == "Moderate") {
                conditionTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorModerate))
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

        btnCart.setOnClickListener {
            //Navigate to Cart
            val navController = this.findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
        }

        btnBack.setOnClickListener {
            // Use the FragmentManager to pop the current fragment from the back stack
            fragmentManager?.popBackStack()
        }

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

    private fun addToCart(product: Product) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID") // Replace with your user ID

        userCart.child(product.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // If the item already exists in the cart, you can show a message or take appropriate action.
                        cartLoadListener.onLoadCartFailed("Item already added to cart")
                    } else {
                        // If the item is not in the cart, add it.
                        val cartModel = Cart()
                        cartModel.key = product.key
                        cartModel.productName = product.productName
                        cartModel.productImage = product.productImage
                        cartModel.productPrice = product.productPrice

                        userCart.child(product.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartLoadListener?.onLoadCartFailed("Item added to cart")
                                countCartFromFirebase()
                            }
                            .addOnFailureListener { e ->
                               onLoadCartFailed(e.message)
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener?.onLoadCartFailed(error.message)
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


}
