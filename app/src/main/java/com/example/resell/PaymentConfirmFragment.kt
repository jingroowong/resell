import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.Order
import com.example.resell.database.OrderDetails
import com.example.resell.database.OrderViewModel
import com.example.resell.database.Payment
import com.example.resell.database.Product
import com.example.resell.databinding.FragmentPaymentConfirmBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date


class PaymentConfirmFragment : Fragment() {
    private lateinit var binding: FragmentPaymentConfirmBinding
    private var paymentAmount: Double? = 0.0
    private var phoneNum: String? = null
    private var currentOrderID: Int? = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val orderViewModel = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        currentOrderID = orderViewModel.orderID
        Log.d("Debug", "Order ID in Payment Confirm Fragment ${currentOrderID}")
        // Retrieve payment from arguments
        paymentAmount = arguments?.getDouble("paymentAmount")
        phoneNum = arguments?.getString("phoneNum")

        // Inflate the layout for this fragment using View Binding
        binding = FragmentPaymentConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val paymentID = System.currentTimeMillis()
        // Create a Payment object
        val payment = Payment(
            paymentID = paymentID.toInt(),
            paymentDate = getCurrentTimestampAsString(),
            paymentAmount = paymentAmount!!,
            paymentType = "Touch 'N Go"
        )

        // Define the country code
        val countryCode = "+60"

        // Extract the last four characters of the user input
        val lastFourDigits = phoneNum!!.takeLast(4)

        // Create the formatted phone number
        val formattedPhoneNumber = "$countryCode*****$lastFourDigits"


        // Populate UI with payment details
        binding.phoneNumberTextView.text = formattedPhoneNumber
        binding.paymentAmountTextView.text = String.format("RM %.2f", payment.paymentAmount)
        binding.paymentIdTextView.text = "${payment.paymentID}"
        binding.paymentDateTextView.text = "${payment.paymentDate}"

        binding.payButton.setOnClickListener {
            // Save the payment to Firebase Realtime Database
            savePaymentToFirebase(payment)

            //Update the order ti Firebase
            updateOrderToFirebase(payment)

            //Update the product to Firebase
            updateProductToFirebase()

            // Navigate to a payment success or confirmation screen
            findNavController().navigate(R.id.action_paymentConfirmFragment_to_paymentSuccessFragment)
        }
    }

    private fun updateProductToFirebase() {
        val orderDetailsRef = FirebaseDatabase.getInstance().getReference("OrderDetail")
        val orderDetailsQuery =
            orderDetailsRef.orderByChild("orderID").equalTo(currentOrderID!!.toDouble())

        orderDetailsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(orderDetailsSnapshot: DataSnapshot) {
                for (orderDetailDataSnapshot in orderDetailsSnapshot.children) {
                    val orderDetail = orderDetailDataSnapshot.getValue(OrderDetails::class.java)
                    if (orderDetail != null) {
                        val productID = orderDetail.productID.toString()

                        val productRef =
                            FirebaseDatabase.getInstance().getReference("Products").child(productID)

                        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(productSnapshot: DataSnapshot) {
                                val product = productSnapshot.getValue(Product::class.java)
                                if (product != null) {
                                    // 3. Set product.productAvailability to false
                                    product.productAvailability = false

                                    // Update the product in Firebase
                                    productRef.setValue(product)
                                }
                            }

                            override fun onCancelled(productError: DatabaseError) {
                                // Handle errors if necessary
                            }
                        })
                    }
                }
            }

            override fun onCancelled(orderDetailsError: DatabaseError) {
                // Handle errors if necessary
            }
        })

    }

    private fun updateOrderToFirebase(payment: Payment) {
        val orderRef = FirebaseDatabase.getInstance().getReference("Orders")
        val orderQuery = orderRef.orderByChild("orderID").equalTo(currentOrderID!!.toDouble())

        orderQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(orderSnapshot: DataSnapshot) {
                for (orderDataSnapshot in orderSnapshot.children) {
                    val order = orderDataSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        // 2.1. Set order.DEAL to true
                        order.deal = true

                        // 2.2. Set order.DATE to today (you can use your date formatting logic)
                        order.orderDate = getCurrentTimestampAsString()

                        // 2.3. Set order.paymentID to payment.paymentID
                        order.paymentID = payment.paymentID

                        // 2.4. Set order.orderAmount to payment.paymentAmount
                        order.orderAmount = payment.paymentAmount

                        // Update the order in Firebase
                        orderRef.child(order.orderID.toString()).setValue(order)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if necessary
            }
        })

    }

    private fun savePaymentToFirebase(payment: Payment) {
        // Push the payment object to Firebase
        val paymentRef = FirebaseDatabase.getInstance().getReference("Payments")
            .child(payment.paymentID.toString())
        paymentRef.setValue(payment)

    }

    private fun getCurrentTimestampAsString(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(date)
    }
}
