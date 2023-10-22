package com.example.bottomnavyt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BorrowProductActivity : AppCompatActivity() {

    private lateinit var product: Product
    private lateinit var tvTotalCost: TextView
    private lateinit var tvSelectedDays: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_product)

        product = intent.getParcelableExtra("product")!!

        val tvProductName: TextView = findViewById(R.id.tvProductName)
        val seekBarDays: SeekBar = findViewById(R.id.seekBarDays)
        tvSelectedDays = findViewById(R.id.tvSelectedDays)
        tvTotalCost = findViewById(R.id.tvTotalCost)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)

        tvProductName.text = product.name

        // Set initial values
        tvSelectedDays.text = "Number of Days: 1"
        tvTotalCost.text = "Total Cost: ${product.pricePerDay}$"

        // SeekBar listener to update days and cost in real-time
        seekBarDays.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val days = progress
                tvSelectedDays.text = "Number of Days: $days"
                val totalCost = days * product.pricePerDay
                tvTotalCost.text = "Total Cost: $totalCost$"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSubmit.setOnClickListener {
            val days = seekBarDays.progress
            if (days == 0) {
                Toast.makeText(this, "Please choose the number of days you want to borrow for.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val returnIntent = Intent()
            returnIntent.putExtra("numDays", days)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }
}
