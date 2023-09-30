import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.Payment
import com.example.resell.databinding.FragmentPaymentBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date

class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding

    private var paymentAmount: Double? = 0.0
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
        // Inflate the layout for this fragment using View Binding
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewAmount.text=String.format("RM %.2f",paymentAmount)

        // Set an OnClickListener for the Log In button
        binding.buttonLogIn.setOnClickListener {

            // Read user input for mobile number and PIN
            val mobileNumber = binding.editTextMobileNumber.text.toString()
            val pin = binding.editTextPIN.text.toString()
            // After validate the payment, navigate to the payment confirmation fragment
            val bundle = Bundle()
            bundle.putDouble("paymentAmount", paymentAmount!!)
            bundle.putString("phoneNum", mobileNumber!!)
            bundle.putInt("userID", currentUserID!!)
            val navController =
                this.findNavController().navigate(R.id.action_paymentFragment_to_paymentConfirmFragment,bundle)
        }
    }

}
