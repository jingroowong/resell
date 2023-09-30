import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.resell.R
import com.example.resell.database.Payment
import com.example.resell.databinding.FragmentPaymentBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date

class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding

    private var paymentAmount: Double? = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve userID from arguments
        paymentAmount = arguments?.getDouble("paymentAmount")
        // Inflate the layout for this fragment using View Binding
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set an OnClickListener for the Log In button
        binding.buttonLogIn.setOnClickListener {
            // Read user input for mobile number and PIN
            val mobileNumber = binding.editTextMobileNumber.text.toString()
            val pin = binding.editTextPIN.text.toString()
            val paymentID = System.currentTimeMillis()
            // Create a Payment object
            val payment = Payment(
                paymentID = paymentID.toInt(),
                paymentDate = getCurrentTimestampAsString(),
                paymentAmount = paymentAmount!!,
                paymentType = "Touch 'N Go"
            )
            // Save the payment to Firebase Realtime Database
            savePaymentToFirebase(payment)
            // After creating the payment, navigate to the payment confirmation fragment
//        val action = PaymentFragmentDirections.actionPaymentFragmentToPaymentConfirmFragment(payment)
//        findNavController().navigate(action)
        }
    }
    private fun savePaymentToFirebase(payment: Payment) {
        // Push the payment object to Firebase
        val paymentRef = FirebaseDatabase.getInstance().getReference("Payments").child(payment.paymentID.toString())
        paymentRef.setValue(payment)

    }

    private fun getCurrentTimestampAsString(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(date)
    }
}
