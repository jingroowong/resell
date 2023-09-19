package com.example.resell.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.AppDatabase
import com.example.resell.database.Product
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminViewProduct.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminViewProduct : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_admin_view_product, container, false)
//        val binding: FragmentGameOverBinding = DataBindingUtil.inflate(
//            inflater, R.layout.fragment_game_over, container, false)

        val application = requireNotNull(this.activity).application
        val productDao = AppDatabase.getInstance(application).productDao
        val date = Date().time.toLong()
        val product = Product(
            productName = "product1",
            productPrice = 11.00,
            productDesc = "New Product",
            productCondition = "New",
            productImage = "Image.jpg",
            dateUpload = date,
            productAvailability = true
        )

        productDao.insert(product)

        val p2 = productDao.get(1)

        val resultTextView = rootView.findViewById<TextView>(R.id.result)


        resultTextView.text = "Hi"
        return inflater.inflate(R.layout.fragment_admin_view_product, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminViewProduct.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminViewProduct().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}