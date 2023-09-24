import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.adapter.MyCartAdapter
import com.example.resell.database.Cart
import com.example.resell.eventbus.UpdateCartEvent
import com.example.resell.listener.ICartLoadListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.example.resell.databinding.FragmentCartBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CartFragment : Fragment(), ICartLoadListener {
    private lateinit var binding: FragmentCartBinding
    private var cartLoadListener: ICartLoadListener? = null
    private lateinit var recyclerCart: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerCart = binding.recyclerCart

        init()
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
                    cartLoadListener?.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener?.onLoadCartFailed(error.message)
                }
            })
    }

    private fun init() {
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerCart.layoutManager = layoutManager
        recyclerCart.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))

        binding.btnBack.setOnClickListener {
            // Use the FragmentManager to pop the current fragment from the back stack
            fragmentManager?.popBackStack()
        }
    }

    override fun onLoadCartSuccess(cartList: List<Cart>) {
        var sum = 0.0
        for (cartModel in cartList) {
            sum += cartModel.productPrice ?: 0.0
        }
        binding.txtTotal.text =  String.format("RM %.2f", sum)
        val adapter = MyCartAdapter(requireContext(), cartList)
        recyclerCart.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(requireView(), message!!, Snackbar.LENGTH_LONG).show()
    }
}
