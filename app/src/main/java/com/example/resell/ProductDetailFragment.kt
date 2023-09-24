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

class ProductDetailFragment : Fragment() {
    private var cartListener: ICartLoadListener? = null
    companion object {
        private const val ARG_PRODUCT = "product"
        private const val ARG_CART_LISTENER = "cartListener"
        @JvmStatic
        fun newInstance(product: Product, cartListener: ICartLoadListener) =
            ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PRODUCT, product)
                    putSerializable(ARG_CART_LISTENER, cartListener)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract cartListener from fragment arguments, if it's passed
        arguments?.let {
            cartListener = it.getSerializable(ARG_CART_LISTENER) as? ICartLoadListener
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    private fun addToCart(product: Product) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(product.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) { //If exist
                        val cartModel = snapshot.getValue(Cart::class.java)
                        val updateData: MutableMap<String, Any> = HashMap()

                        userCart.child(cartModel!!.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                cartListener?.onLoadCartFailed("Item already added")
                            }
                            .addOnFailureListener { e -> cartListener?.onLoadCartFailed(e.message) }
                        EventBus.getDefault().postSticky(UpdateCartEvent())
                        Log.d("FirebaseData", "Cart Data retrieved successfully")

                    } else { //If item not in cart, add new
                        val cartModel = Cart()
                        cartModel.key = product.key
                        cartModel.productName = product.productName
                        cartModel.productImage = product.productImage
                        cartModel.productPrice = product.productPrice

                        userCart.child(product.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener?.onLoadCartFailed("Success add to cart")
                            }
                            .addOnFailureListener { e ->
                                cartListener?.onLoadCartFailed(e.message)
                            }
                        Log.d("FirebaseData", "No data found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener?.onLoadCartFailed(error.message)
                }

            })
    }
}
