import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.databinding.FragmentPaymentSuccessBinding


class PaymentSuccessFragment : Fragment() {
    private lateinit var binding: FragmentPaymentSuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentPaymentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle button click to return to the main screen
        binding.buttonReturnToMain.setOnClickListener {
            this.findNavController().navigate(R.id.action_paymentSuccessFragment_to_productFragment)
        }
    }
}
