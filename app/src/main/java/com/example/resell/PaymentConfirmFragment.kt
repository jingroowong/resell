import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.Payment
import com.example.resell.databinding.FragmentPaymentConfirmBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date


class PaymentConfirmFragment : Fragment() {
    private lateinit var binding: FragmentPaymentConfirmBinding
    private var paymentAmount: Double? = 0.0
    private var phoneNum: String? = null
    // Add a variable to store the current userID
    private var currentUserID: Int? = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve userID from arguments
        currentUserID = arguments?.getInt("userID")
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
        binding.paymentAmountTextView.text = "RM ${payment.paymentAmount}"
        binding.paymentIdTextView.text = "${payment.paymentID}"
        binding.paymentDateTextView.text = "${payment.paymentDate}"

        binding.payButton.setOnClickListener {
            // Save the payment to Firebase Realtime Database
            savePaymentToFirebase(payment)
            // TODO: Update your database with payment details, set order.DEAL to true, and product.productAvailability to false
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
