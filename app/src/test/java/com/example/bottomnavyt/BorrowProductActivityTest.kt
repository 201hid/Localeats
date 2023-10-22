package com.example.bottomnavyt

import org.junit.Assert.*
import org.junit.Test

class BorrowProductActivityTest {

    @Test
    fun testCalculateTotalCost() {
        val product = Product("TestProduct", 4f, listOf("Attribute1"), 10.0)
        val days = 5
        val expectedTotalCost = 50.0
        val actualTotalCost = BorrowProductActivity().calculateTotalCost(product, days)
        assertEquals(expectedTotalCost, actualTotalCost, 0.0)
    }
}
