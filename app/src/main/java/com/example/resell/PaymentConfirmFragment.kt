import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.OrderDetail
import com.example.resell.database.Payment
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
    // Add a variable to store the current userID
    private var currentUserID: Int? = 0
    private var currentOrderID: Int? = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve userID from arguments
        currentUserID = arguments?.getInt("userID")
        // Retrieve orderID from arguments
        currentOrderID = arguments?.getInt("orderID")
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
        binding.paymentAmountTextView.text = String.format("RM %.2f",payment.paymentAmount)
        binding.paymentIdTextView.text = "${payment.paymentID}"
        binding.paymentDateTextView.text = "${payment.paymentDate}"

        binding.payButton.setOnClickListener {
            // Save the payment to Firebase Realtime Database
            savePaymentToFirebase(payment)
            // TODO: Update your database with payment details, set order.DEAL to true, and product.productAvailability to false
            // Create a reference to the Firebase Realtime Database for orders
            val orderRef = FirebaseDatabase.getInstance().getReference("Orders")

// Update the order.DEAL value to true for the current order
            orderRef.child(currentOrderID.toString()).child("deal").setValue(true)
            Log.d("Firebase","Order ID : "+currentOrderID.toString())

            val orderDetailsRef = FirebaseDatabase.getInstance().getReference("OrderDetail")
            val orderDetailQuery = orderDetailsRef.orderByChild("orderID").equalTo(currentOrderID!!.toDouble())

            orderDetailQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(orderDetailsSnapshot: DataSnapshot) {
                    for (orderDetailData in orderDetailsSnapshot.children) {
                        val orderDetail = orderDetailData.getValue(OrderDetail::class.java)
                        if (orderDetail != null) {

                            val productID = orderDetail.productID.toString()
                            Log.d("Firebase", "Product ID" + productID)
                            // Proceed to update the product's availability.

                            // Assuming you already have a reference to the database
                            val productsRef =
                                FirebaseDatabase.getInstance().getReference("Products")

// Replace 'productID' with the actual product ID you obtained from step 1
                            val productRef = productsRef.child(productID)

// Set 'productAvailability' to 'true' (assuming 'true' indicates availability)
                            productRef.child("productAvailability").setValue(false)
                        }
                    }
                }

                override fun onCancelled(orderDetailsError: DatabaseError) {
                    // Handle the error
                }
            })



            val bundle = Bundle()
            bundle.putInt("userID", currentUserID!!)
            // Navigate to a payment success or confirmation screen
            this.findNavController().navigate(R.id.action_paymentConfirmFragment_to_paymentSuccessFragment,bundle)
        }
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
