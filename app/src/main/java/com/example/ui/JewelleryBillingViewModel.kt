package com.example.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AshaDatabase
import com.example.data.InvoiceEntity
import com.example.data.InvoiceRepository
import com.example.model.CustomerInfo
import com.example.model.JewelleryItem
import com.example.model.MakingChargeType
import com.example.model.OldGoldExchange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BillingUiState(
    val goldPricePerGramStr: String = "7250",
    val items: List<JewelleryItem> = listOf(
        JewelleryItem(name = "Item 1", weightGramsStr = "8.450", makingChargeType = MakingChargeType.PERCENTAGE, makingValueStr = "12", otherChargesStr = "0")
    ),
    val oldGold: OldGoldExchange = OldGoldExchange(weightGramsStr = "", ratePerGramStr = "6850", purityPercentStr = "100"),
    val customerInfo: CustomerInfo = CustomerInfo(
        invoiceNumber = generateInitialInvoiceNumber()
    ),
    val applyGst: Boolean = true,
    val gstPercentStr: String = "3.0",
    val searchQuery: String = "",
    val isHistoryOpen: Boolean = false,
    val isPreviewDialogOpen: Boolean = false
) {
    val goldPricePerGram: Double
        get() = goldPricePerGramStr.toDoubleOrNull() ?: 0.0

    val gstPercent: Double
        get() = if (applyGst) (gstPercentStr.toDoubleOrNull() ?: 3.0) else 0.0

    val totalWeightGrams: Double
        get() = items.sumOf { it.weightGrams }

    val totalGoldValue: Double
        get() = items.sumOf { it.calculateGoldValue(goldPricePerGram) }

    val totalMakingCharges: Double
        get() = items.sumOf { it.calculateMakingAmount(goldPricePerGram) }

    val totalOtherCharges: Double
        get() = items.sumOf { it.otherCharges }

    val grossAmount: Double
        get() = items.sumOf { it.calculateTotalAmount(goldPricePerGram) }

    val gstAmount: Double
        get() = grossAmount * (gstPercent / 100.0)

    val totalWithGst: Double
        get() = grossAmount + gstAmount

    val oldGoldDeduction: Double
        get() = oldGold.totalValue

    val netPayable: Double
        get() = maxOf(0.0, totalWithGst - oldGoldDeduction)
}

private fun generateInitialInvoiceNumber(): String {
    val df = SimpleDateFormat("yyMMdd-HHmm", Locale.US)
    return "AJ-${df.format(Date())}"
}

class JewelleryBillingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InvoiceRepository

    init {
        val db = AshaDatabase.getDatabase(application)
        repository = InvoiceRepository(db.invoiceDao())
    }

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    private val _searchFilter = MutableStateFlow("")
    val savedInvoices: StateFlow<List<InvoiceEntity>> = combine(
        repository.allInvoices,
        _searchFilter
    ) { invoices, filter ->
        if (filter.isBlank()) invoices
        else invoices.filter {
            it.invoiceNumber.contains(filter, ignoreCase = true) ||
            it.customerName.contains(filter, ignoreCase = true) ||
            it.customerPhone.contains(filter, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateGoldPrice(priceStr: String) {
        _uiState.update { it.copy(goldPricePerGramStr = priceStr) }
    }

    fun setQuickGoldRate(price: Double) {
        _uiState.update { it.copy(goldPricePerGramStr = price.toInt().toString()) }
    }

    fun addItem() {
        _uiState.update { state ->
            val nextIndex = state.items.size + 1
            val newItem = JewelleryItem(
                name = "Item $nextIndex",
                weightGramsStr = "",
                makingChargeType = MakingChargeType.PERCENTAGE,
                makingValueStr = "10",
                otherChargesStr = ""
            )
            state.copy(items = state.items + newItem)
        }
    }

    fun removeItem(index: Int) {
        _uiState.update { state ->
            if (state.items.size > 1 && index in state.items.indices) {
                val updated = state.items.toMutableList().apply { removeAt(index) }
                state.copy(items = updated)
            } else state
        }
    }

    fun updateItemName(index: Int, name: String) {
        _uiState.update { state ->
            if (index in state.items.indices) {
                val updated = state.items.toMutableList()
                updated[index] = updated[index].copy(name = name)
                state.copy(items = updated)
            } else state
        }
    }

    fun updateItemWeight(index: Int, weightStr: String) {
        _uiState.update { state ->
            if (index in state.items.indices) {
                val updated = state.items.toMutableList()
                updated[index] = updated[index].copy(weightGramsStr = weightStr)
                state.copy(items = updated)
            } else state
        }
    }

    fun updateItemMakingType(index: Int, type: MakingChargeType) {
        _uiState.update { state ->
            if (index in state.items.indices) {
                val updated = state.items.toMutableList()
                updated[index] = updated[index].copy(makingChargeType = type)
                state.copy(items = updated)
            } else state
        }
    }

    fun updateItemMakingValue(index: Int, valueStr: String) {
        _uiState.update { state ->
            if (index in state.items.indices) {
                val updated = state.items.toMutableList()
                updated[index] = updated[index].copy(makingValueStr = valueStr)
                state.copy(items = updated)
            } else state
        }
    }

    fun updateItemOtherCharges(index: Int, otherStr: String) {
        _uiState.update { state ->
            if (index in state.items.indices) {
                val updated = state.items.toMutableList()
                updated[index] = updated[index].copy(otherChargesStr = otherStr)
                state.copy(items = updated)
            } else state
        }
    }

    fun updateOldGoldWeight(weight: String) {
        _uiState.update { it.copy(oldGold = it.oldGold.copy(weightGramsStr = weight)) }
    }

    fun updateOldGoldRate(rate: String) {
        _uiState.update { it.copy(oldGold = it.oldGold.copy(ratePerGramStr = rate)) }
    }

    fun updateOldGoldPurity(purity: String) {
        _uiState.update { it.copy(oldGold = it.oldGold.copy(purityPercentStr = purity)) }
    }

    fun updateCustomerName(name: String) {
        _uiState.update { it.copy(customerInfo = it.customerInfo.copy(name = name)) }
    }

    fun updateCustomerPhone(phone: String) {
        _uiState.update { it.copy(customerInfo = it.customerInfo.copy(phone = phone)) }
    }

    fun updateInvoiceNumber(num: String) {
        _uiState.update { it.copy(customerInfo = it.customerInfo.copy(invoiceNumber = num)) }
    }

    fun toggleGst(apply: Boolean) {
        _uiState.update { it.copy(applyGst = apply) }
    }

    fun updateGstPercent(percent: String) {
        _uiState.update { it.copy(gstPercentStr = percent) }
    }

    fun setPreviewDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isPreviewDialogOpen = open) }
    }

    fun setHistoryOpen(open: Boolean) {
        _uiState.update { it.copy(isHistoryOpen = open) }
    }

    fun setSearchQuery(query: String) {
        _searchFilter.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun resetBill() {
        _uiState.update {
            BillingUiState(
                goldPricePerGramStr = it.goldPricePerGramStr,
                customerInfo = CustomerInfo(invoiceNumber = generateInitialInvoiceNumber())
            )
        }
    }

    fun saveCurrentInvoice() {
        val state = _uiState.value
        if (state.items.isEmpty() || state.totalWeightGrams <= 0.0) {
            Toast.makeText(getApplication(), "Please enter gold weight for at least one item", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            val entity = InvoiceEntity(
                invoiceNumber = state.customerInfo.invoiceNumber.ifBlank { generateInitialInvoiceNumber() },
                customerName = state.customerInfo.name.ifBlank { "Cash Customer" },
                customerPhone = state.customerInfo.phone,
                dateTimestamp = System.currentTimeMillis(),
                goldRatePerGram = state.goldPricePerGram,
                itemsJson = InvoiceEntity.serializeItems(state.items),
                totalWeightGrams = state.totalWeightGrams,
                totalGoldValue = state.totalGoldValue,
                totalMakingCharges = state.totalMakingCharges,
                totalOtherCharges = state.totalOtherCharges,
                grossAmount = state.grossAmount,
                gstPercent = state.gstPercent,
                gstAmount = state.gstAmount,
                totalAmount = state.totalWithGst,
                oldGoldWeight = state.oldGold.weightGrams,
                oldGoldRate = state.oldGold.ratePerGram,
                oldGoldValue = state.oldGoldDeduction,
                netPayable = state.netPayable
            )
            repository.insertInvoice(entity)
            Toast.makeText(getApplication(), "Invoice #${entity.invoiceNumber} saved to History", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadInvoice(invoice: InvoiceEntity) {
        val parsedItems = invoice.parseItems()
        _uiState.update { state ->
            state.copy(
                goldPricePerGramStr = if (invoice.goldRatePerGram > 0) invoice.goldRatePerGram.toInt().toString() else state.goldPricePerGramStr,
                items = if (parsedItems.isNotEmpty()) parsedItems else state.items,
                oldGold = OldGoldExchange(
                    weightGramsStr = if (invoice.oldGoldWeight > 0) invoice.oldGoldWeight.toString() else "",
                    ratePerGramStr = if (invoice.oldGoldRate > 0) invoice.oldGoldRate.toInt().toString() else "6850",
                    purityPercentStr = "100"
                ),
                customerInfo = CustomerInfo(
                    name = invoice.customerName,
                    phone = invoice.customerPhone,
                    invoiceNumber = invoice.invoiceNumber
                ),
                applyGst = invoice.gstAmount > 0,
                gstPercentStr = if (invoice.gstPercent > 0) invoice.gstPercent.toString() else "3.0",
                isHistoryOpen = false
            )
        }
        Toast.makeText(getApplication(), "Loaded Invoice #${invoice.invoiceNumber}", Toast.LENGTH_SHORT).show()
    }

    fun deleteInvoice(id: Long) {
        viewModelScope.launch {
            repository.deleteInvoice(id)
            Toast.makeText(getApplication(), "Invoice deleted", Toast.LENGTH_SHORT).show()
        }
    }
}
