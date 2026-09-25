package com.example.model

import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

enum class MakingChargeType(val label: String, val symbol: String) {
    PERCENTAGE("Percentage (%)", "%"),
    FIXED_AMOUNT("Fixed (₹)", "₹")
}

data class JewelleryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Item",
    val weightGramsStr: String = "",
    val makingChargeType: MakingChargeType = MakingChargeType.PERCENTAGE,
    val makingValueStr: String = "",
    val otherChargesStr: String = ""
) {
    val weightGrams: Double
        get() = weightGramsStr.toDoubleOrNull() ?: 0.0

    val makingValue: Double
        get() = makingValueStr.toDoubleOrNull() ?: 0.0

    val otherCharges: Double
        get() = otherChargesStr.toDoubleOrNull() ?: 0.0

    fun calculateGoldValue(goldPricePerGram: Double): Double {
        return weightGrams * goldPricePerGram
    }

    fun calculateMakingAmount(goldPricePerGram: Double): Double {
        val goldVal = calculateGoldValue(goldPricePerGram)
        return when (makingChargeType) {
            MakingChargeType.PERCENTAGE -> (goldVal * makingValue) / 100.0
            MakingChargeType.FIXED_AMOUNT -> makingValue
        }
    }

    fun calculateTotalAmount(goldPricePerGram: Double): Double {
        return calculateGoldValue(goldPricePerGram) +
                calculateMakingAmount(goldPricePerGram) +
                otherCharges
    }
}

data class OldGoldExchange(
    val weightGramsStr: String = "",
    val ratePerGramStr: String = "",
    val purityPercentStr: String = "100"
) {
    val weightGrams: Double
        get() = weightGramsStr.toDoubleOrNull() ?: 0.0

    val ratePerGram: Double
        get() = ratePerGramStr.toDoubleOrNull() ?: 0.0

    val purityPercent: Double
        get() = (purityPercentStr.toDoubleOrNull() ?: 100.0).coerceIn(0.0, 100.0)

    val totalValue: Double
        get() = weightGrams * ratePerGram * (purityPercent / 100.0)
}

data class CustomerInfo(
    val name: String = "",
    val phone: String = "",
    val invoiceNumber: String = "",
    val notes: String = ""
)

object CurrencyHelper {
    private val indianFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    fun formatInr(amount: Double): String {
        return indianFormat.format(amount).replace("INR", "₹").trim()
    }

    fun formatGrams(grams: Double): String {
        return String.format(Locale.US, "%.3f g", grams)
    }
}
