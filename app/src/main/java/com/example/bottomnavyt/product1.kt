//package com.example.bottomnavyt
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.RatingBar
//import android.widget.TextView
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//
//private const val REQUEST_CODE = 1234
//
//class product1 : Fragment() {
//
//    private val productData = Product("Honda Civic", 2f, listOf("Timeless Design, Civic Pride"), 50.0)
//    private lateinit var tvDueDate: TextView
//    private lateinit var btnBorrow: Button
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setRetainInstance(true)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_product1, container, false)
//
//        tvDueDate = view.findViewById(R.id.tvDueDate)
//        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
//        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
//        val tvAttributes: TextView = view.findViewById(R.id.tvAttributes)
//        val tvPricePerDay: TextView = view.findViewById(R.id.tvPricePerDay)
//        val productImageView : ImageView = view.findViewById(R.id.productImage)
//
//        btnBorrow = view.findViewById(R.id.btnBorrow)
//
//        tvProductName.text = productData.name
//        ratingBar.rating = productData.rating
//        tvAttributes.text = productData.attributes.joinToString(", ")
//        tvPricePerDay.text = "Price per day: ${productData.pricePerDay}$"
//
//        productImageView.setImageResource(R.drawable.honda_civic)  // <-- Set your image here
//
//
//        btnBorrow.setOnClickListener {
//            val intent = Intent(context, BorrowProductActivity::class.java)
//            intent.putExtra("product", productData)
//            startActivityForResult(intent, REQUEST_CODE)
//        }
//
//        updateBorrowButtonVisibility()
//
//        return view
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val numDays = data?.getIntExtra("numDays", 0)
//            arguments = Bundle().apply {
//                putInt("numDays", numDays ?: 0)
//            }
//
//            // Calculate the due date
//            val dueDate = Calendar.getInstance().apply {
//                add(Calendar.DAY_OF_YEAR, numDays ?: 0)
//            }
//
//            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//            tvDueDate.text = "Due by: ${dateFormat.format(dueDate.time)}"
//            updateBorrowButtonVisibility()
//        }
//    }
//
//    private fun updateBorrowButtonVisibility() {
//        if (tvDueDate.text.isNullOrEmpty()) {
//            btnBorrow.visibility = View.VISIBLE
//        } else {
//            btnBorrow.visibility = View.GONE
//        }
//    }
//}
