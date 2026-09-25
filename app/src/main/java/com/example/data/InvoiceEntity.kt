package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject
import com.example.model.JewelleryItem
import com.example.model.MakingChargeType

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val dateTimestamp: Long,
    val goldRatePerGram: Double,
    val itemsJson: String,
    val totalWeightGrams: Double,
    val totalGoldValue: Double,
    val totalMakingCharges: Double,
    val totalOtherCharges: Double,
    val grossAmount: Double,
    val gstPercent: Double,
    val gstAmount: Double,
    val totalAmount: Double,
    val oldGoldWeight: Double,
    val oldGoldRate: Double,
    val oldGoldValue: Double,
    val netPayable: Double
) {
    fun parseItems(): List<JewelleryItem> {
        val list = mutableListOf<JewelleryItem>()
        try {
            val jsonArray = JSONArray(itemsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val typeStr = obj.optString("makingChargeType", MakingChargeType.PERCENTAGE.name)
                val type = try {
                    MakingChargeType.valueOf(typeStr)
                } catch (e: Exception) {
                    MakingChargeType.PERCENTAGE
                }
                list.add(
                    JewelleryItem(
                        id = obj.optString("id"),
                        name = obj.optString("name", "Item ${i + 1}"),
                        weightGramsStr = obj.optString("weightGramsStr", "0"),
                        makingChargeType = type,
                        makingValueStr = obj.optString("makingValueStr", "0"),
                        otherChargesStr = obj.optString("otherChargesStr", "0")
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }

    companion object {
        fun serializeItems(items: List<JewelleryItem>): String {
            val array = JSONArray()
            for (item in items) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("name", item.name)
                obj.put("weightGramsStr", item.weightGramsStr)
                obj.put("makingChargeType", item.makingChargeType.name)
                obj.put("makingValueStr", item.makingValueStr)
                obj.put("otherChargesStr", item.otherChargesStr)
                array.put(obj)
            }
            return array.toString()
        }
    }
}
