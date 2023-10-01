import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.OrderViewModel
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

            // Validate mobile number
            val mobileNumberPattern = "\\d{9,10}".toRegex()
            val isMobileNumberValid = mobileNumber.matches(mobileNumberPattern)

            // Validate PIN
            val pinPattern = "\\d{6}".toRegex()
            val isPinValid = pin.matches(pinPattern)

            if (isMobileNumberValid && isPinValid) {
                // After validate the payment, navigate to the payment confirmation fragment
                val bundle = Bundle()
                bundle.putString("phoneNum", mobileNumber!!)
                bundle.putDouble("paymentAmount", paymentAmount!!)
                this.findNavController().navigate(R.id.action_paymentFragment_to_paymentConfirmFragment,bundle)
            } else {
                // Display an error message to the user
                if (!isMobileNumberValid) {
                    binding.editTextMobileNumber.error = "Invalid mobile number"
                }
                if (!isPinValid) {
                    binding.editTextPIN.error = "Invalid PIN"
                }
            }


        }
    }

}
