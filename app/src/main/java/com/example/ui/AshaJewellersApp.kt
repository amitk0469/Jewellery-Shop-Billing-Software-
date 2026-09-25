package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CurrencyHelper
import com.example.model.JewelleryItem
import com.example.model.MakingChargeType
import com.example.ui.components.DarkActionButton
import com.example.ui.components.GlassInputField
import com.example.ui.components.GoldBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.HistorySheet
import com.example.ui.components.InvoicePrintDialog
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.liquidGlassBackground
import com.example.ui.theme.*

@Composable
fun AshaJewellersApp(
    viewModel: JewelleryBillingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val savedInvoices by viewModel.savedInvoices.collectAsState()
    val scrollState = rememberScrollState()

    var showCustomerDetails by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .liquidGlassBackground()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // HEADER
            HeaderSection(
                onOpenHistory = { viewModel.setHistoryOpen(true) },
                onReset = { viewModel.resetBill() },
                onSave = { viewModel.saveCurrentInvoice() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // CUSTOMER INFO (Collapsible / expandable)
            CustomerInfoCard(
                state = state,
                isExpanded = showCustomerDetails,
                onToggleExpand = { showCustomerDetails = !showCustomerDetails },
                onNameChange = { viewModel.updateCustomerName(it) },
                onPhoneChange = { viewModel.updateCustomerPhone(it) },
                onInvoiceNumberChange = { viewModel.updateInvoiceNumber(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // GOLD PRICE PER GRAM
            GoldPriceCard(
                goldPriceStr = state.goldPricePerGramStr,
                onGoldPriceChange = { viewModel.updateGoldPrice(it) },
                onPresetSelected = { viewModel.setQuickGoldRate(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ITEMS LIST
            state.items.forEachIndexed { index, item ->
                JewelleryItemCard(
                    itemNumber = index + 1,
                    item = item,
                    goldPrice = state.goldPricePerGram,
                    canRemove = state.items.size > 1,
                    onNameChange = { viewModel.updateItemName(index, it) },
                    onWeightChange = { viewModel.updateItemWeight(index, it) },
                    onMakingTypeChange = { viewModel.updateItemMakingType(index, it) },
                    onMakingValueChange = { viewModel.updateItemMakingValue(index, it) },
                    onOtherChargesChange = { viewModel.updateItemOtherCharges(index, it) },
                    onRemove = { viewModel.removeItem(index) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ADD ITEM BUTTON
            GoldGradientButton(
                text = "＋ Add Item ${state.items.size + 1}",
                onClick = { viewModel.addItem() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // OLD GOLD EXCHANGE CARD
            OldGoldExchangeCard(
                weightStr = state.oldGold.weightGramsStr,
                rateStr = state.oldGold.ratePerGramStr,
                purityStr = state.oldGold.purityPercentStr,
                deductionAmount = state.oldGoldDeduction,
                onWeightChange = { viewModel.updateOldGoldWeight(it) },
                onRateChange = { viewModel.updateOldGoldRate(it) },
                onPurityChange = { viewModel.updateOldGoldPurity(it) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // BILL SUMMARY CARD
            BillSummaryCard(
                state = state,
                onToggleGst = { viewModel.toggleGst(it) },
                onGstPercentChange = { viewModel.updateGstPercent(it) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // TOTAL PAYABLE PROMINENT CARD
            PayableAmountCard(payableAmount = state.netPayable)

            Spacer(modifier = Modifier.height(14.dp))

            // NOTICE / HALLMARKING CARD
            NoticeCard()

            Spacer(modifier = Modifier.height(14.dp))

            // ACTIONS: GENERATE / PRINT BILL
            DarkActionButton(
                text = "🖨️  VIEW & PRINT TAX INVOICE",
                onClick = {
                    viewModel.saveCurrentInvoice()
                    viewModel.setPreviewDialogOpen(true)
                }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Invoice Print / Share Preview Dialog
    if (state.isPreviewDialogOpen) {
        InvoicePrintDialog(
            state = state,
            onDismiss = { viewModel.setPreviewDialogOpen(false) }
        )
    }

    // Saved Invoices History Sheet
    if (state.isHistoryOpen) {
        HistorySheet(
            invoices = savedInvoices,
            searchQuery = state.searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            onLoadInvoice = { viewModel.loadInvoice(it) },
            onDeleteInvoice = { viewModel.deleteInvoice(it) },
            onDismiss = { viewModel.setHistoryOpen(false) }
        )
    }
}

@Composable
private fun HeaderSection(
    onOpenHistory: () -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    LiquidGlassCard(padding = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick Reset
            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x1F000000))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Form",
                    tint = CharcoalDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Center Diamond Brand
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xF2FFFFFF), Color(0x66FFFFFF))
                            )
                        )
                        .border(1.dp, Color(0xE6FFFFFF), RoundedCornerShape(18.dp))
                        .shadow(6.dp, RoundedCornerShape(18.dp), spotColor = Color(0x1A000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💎",
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "ASHA JEWELLERS",
                    fontWeight = FontWeight.Black,
                    fontSize = 23.sp,
                    color = GoldDeep,
                    letterSpacing = 1.8.sp
                )

                Text(
                    text = "GOLD JEWELLERY BILLING CALCULATOR",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = GrayText,
                    letterSpacing = 0.4.sp
                )
            }

            // Top action buttons: Save & History
            Row {
                IconButton(
                    onClick = onSave,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x1F000000))
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save Bill",
                        tint = GoldDeep,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x1F000000))
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = CharcoalDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerInfoCard(
    state: BillingUiState,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onInvoiceNumberChange: (String) -> Unit
) {
    LiquidGlassCard(padding = 14.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GoldBadge(text = "👤", size = 32.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (state.customerInfo.name.isNotBlank()) state.customerInfo.name else "Customer & Invoice Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CharcoalDark
                    )
                    Text(
                        text = "Bill #${state.customerInfo.invoiceNumber}",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand customer details",
                tint = CharcoalDark
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                GlassInputField(
                    value = state.customerInfo.name,
                    onValueChange = onNameChange,
                    label = "Customer Name",
                    placeholder = "e.g. Rahul Sharma",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassInputField(
                        value = state.customerInfo.phone,
                        onValueChange = onPhoneChange,
                        label = "Mobile Number",
                        placeholder = "e.g. 9876543210",
                        keyboardType = KeyboardType.Phone,
                        modifier = Modifier.weight(1f)
                    )

                    GlassInputField(
                        value = state.customerInfo.invoiceNumber,
                        onValueChange = onInvoiceNumberChange,
                        label = "Invoice No",
                        placeholder = "AJ-XXXX",
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoldPriceCard(
    goldPriceStr: String,
    onGoldPriceChange: (String) -> Unit,
    onPresetSelected: (Double) -> Unit
) {
    LiquidGlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoldBadge(text = "₹")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Gold Price",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        GlassInputField(
            value = goldPriceStr,
            onValueChange = onGoldPriceChange,
            label = "GOLD PRICE PER GRAM (₹)",
            placeholder = "Enter current gold price",
            keyboardType = KeyboardType.Decimal,
            isLargeFont = true,
            textColor = GoldDeep,
            trailingText = "₹/g"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick rate preset chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetChip(label = "24K (999)", rate = 7650.0, onSelect = onPresetSelected)
            PresetChip(label = "22K (916)", rate = 7015.0, onSelect = onPresetSelected)
            PresetChip(label = "18K (750)", rate = 5740.0, onSelect = onPresetSelected)
            PresetChip(label = "14K (585)", rate = 4460.0, onSelect = onPresetSelected)
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    rate: Double,
    onSelect: (Double) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33C79A45))
            .border(1.dp, Color(0x66C79A45), RoundedCornerShape(10.dp))
            .clickable { onSelect(rate) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "$label: ₹${rate.toInt()}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GoldDark
        )
    }
}

@Composable
private fun JewelleryItemCard(
    itemNumber: Int,
    item: JewelleryItem,
    goldPrice: Double,
    canRemove: Boolean,
    onNameChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onMakingTypeChange: (MakingChargeType) -> Unit,
    onMakingValueChange: (String) -> Unit,
    onOtherChargesChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    val goldValue = item.calculateGoldValue(goldPrice)
    val makingAmount = item.calculateMakingAmount(goldPrice)
    val otherCharges = item.otherCharges
    val itemTotal = item.calculateTotalAmount(goldPrice)

    LiquidGlassCard {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GoldBadge(text = "$itemNumber")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = item.name.ifBlank { "Item $itemNumber" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalDark
                )
            }

            if (canRemove) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(RemoveButtonBg)
                        .clickable(onClick = onRemove)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Remove",
                        color = RemoveButtonRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Category Suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Ring", "Necklace", "Bangle", "Chain", "Earring", "Coin", "Pendant").forEach { tag ->
                val isSelected = item.name.equals(tag, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GoldLight else Color(0x22FFFFFF))
                        .border(
                            1.dp,
                            if (isSelected) GoldPrimary else Color(0x1F000000),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onNameChange(tag) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) GoldDark else CharcoalDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // GOLD WEIGHT FIELD
        GlassInputField(
            value = item.weightGramsStr,
            onValueChange = onWeightChange,
            label = "GOLD WEIGHT (GRAM)",
            placeholder = "Enter gold weight in grams",
            keyboardType = KeyboardType.Decimal,
            trailingText = "grams"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // MAKING CHARGES (Type dropdown + Value input)
        Text(
            text = "MAKING CHARGE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E4E4E),
            letterSpacing = 0.4.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown Type Selector
            MakingChargeDropdown(
                selectedType = item.makingChargeType,
                onTypeSelected = onMakingTypeChange,
                modifier = Modifier.width(135.dp)
            )

            // Value Input
            GlassInputField(
                value = item.makingValueStr,
                onValueChange = onMakingValueChange,
                label = "",
                placeholder = if (item.makingChargeType == MakingChargeType.PERCENTAGE) "e.g. 12%" else "₹ amount",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
                trailingText = if (item.makingChargeType == MakingChargeType.PERCENTAGE) "%" else "₹"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // OTHER CHARGES FIELD
        GlassInputField(
            value = item.otherChargesStr,
            onValueChange = onOtherChargesChange,
            label = "OTHER CHARGES (₹) (STONES, PEARLS, HUID)",
            placeholder = "Enter other charges (optional)",
            keyboardType = KeyboardType.Decimal,
            trailingText = "₹"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ITEM RESULT BOX
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x66FFFFFF))
                .border(1.dp, Color(0xB3FFFFFF), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ResultItemRow("Gold Value", CurrencyHelper.formatInr(goldValue))
                ResultItemRow("Making Charge", CurrencyHelper.formatInr(makingAmount))
                if (otherCharges > 0) {
                    ResultItemRow("Other Charges", CurrencyHelper.formatInr(otherCharges))
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                    color = Color(0x1F000000)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.name.ifBlank { "Item $itemNumber" }} Total",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldDeep
                    )
                    Text(
                        text = CurrencyHelper.formatInr(itemTotal),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldDeep
                    )
                }
            }
        }
    }
}

@Composable
private fun MakingChargeDropdown(
    selectedType: MakingChargeType,
    onTypeSelected: (MakingChargeType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        val shape = RoundedCornerShape(14.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(shape)
                .background(Color(0x8CFFFFFF))
                .border(1.dp, Color(0x1A000000), shape)
                .clickable { expanded = true }
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (selectedType == MakingChargeType.PERCENTAGE) "% Percent" else "₹ Fixed",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select type",
                tint = GrayText,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Percentage (%)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                onClick = {
                    onTypeSelected(MakingChargeType.PERCENTAGE)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Fixed (₹)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                onClick = {
                    onTypeSelected(MakingChargeType.FIXED_AMOUNT)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun ResultItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = GrayText)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CharcoalDark)
    }
}

@Composable
private fun OldGoldExchangeCard(
    weightStr: String,
    rateStr: String,
    purityStr: String,
    deductionAmount: Double,
    onWeightChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onPurityChange: (String) -> Unit
) {
    LiquidGlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoldBadge(text = "♺")
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Old Gold Exchange / Scrap",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldDeep
                )
                Text(
                    text = "Deduct scrap gold exchange from bill",
                    fontSize = 11.sp,
                    color = GrayText
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassInputField(
                value = weightStr,
                onValueChange = onWeightChange,
                label = "OLD WEIGHT (G)",
                placeholder = "0.000",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
                trailingText = "g"
            )

            GlassInputField(
                value = rateStr,
                onValueChange = onRateChange,
                label = "SCRAP RATE (₹/G)",
                placeholder = "6850",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
                trailingText = "₹"
            )
        }

        if (deductionAmount > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1F22C55E))
                    .border(1.dp, Color(0x3322C55E), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Old Gold Credit Applied:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF15803D)
                )
                Text(
                    text = "- ${CurrencyHelper.formatInr(deductionAmount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF15803D)
                )
            }
        }
    }
}

@Composable
private fun BillSummaryCard(
    state: BillingUiState,
    onToggleGst: (Boolean) -> Unit,
    onGstPercentChange: (String) -> Unit
) {
    LiquidGlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoldBadge(text = "∑")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Bill Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        SummaryRow("Total Gold Weight", CurrencyHelper.formatGrams(state.totalWeightGrams))
        SummaryRow("Total Gold Value", CurrencyHelper.formatInr(state.totalGoldValue))
        SummaryRow("Total Making Charges", CurrencyHelper.formatInr(state.totalMakingCharges))
        if (state.totalOtherCharges > 0) {
            SummaryRow("Total Other Charges", CurrencyHelper.formatInr(state.totalOtherCharges))
        }

        SummaryRow(
            label = "Gross Amount",
            value = CurrencyHelper.formatInr(state.grossAmount),
            isBold = true
        )

        // GST Row with Switch & percent
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "GST (${state.gstPercent}%)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GrayText
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = state.applyGst,
                    onCheckedChange = onToggleGst,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GoldDeep,
                        checkedTrackColor = GoldLight
                    )
                )
            }

            Text(
                text = CurrencyHelper.formatInr(state.gstAmount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
        }

        if (state.oldGoldDeduction > 0) {
            SummaryRow(
                label = "Old Gold Scrap Deduction",
                value = "- ${CurrencyHelper.formatInr(state.oldGoldDeduction)}",
                isDeduction = true
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isDeduction: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isBold) 15.sp else 14.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal,
            color = if (isBold) CharcoalDark else GrayText
        )
        Text(
            text = value,
            fontSize = if (isBold) 15.sp else 14.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isDeduction) Color(0xFFB52D2D) else CharcoalDark
        )
    }
    HorizontalDivider(color = Color(0x14000000), thickness = 0.8.dp)
}

@Composable
private fun PayableAmountCard(payableAmount: Double) {
    LiquidGlassCard(
        cornerRadius = 24.dp,
        padding = 20.dp
    ) {
        Text(
            text = "TOTAL PAYABLE AMOUNT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = GrayText
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = CurrencyHelper.formatInr(payableAmount),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = GoldDeep,
            letterSpacing = 0.5.sp
        )

        Text(
            text = "Net amount payable inclusive of gold, making charges & taxes",
            fontSize = 11.sp,
            color = GrayText,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun NoticeCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NoticeBackground)
            .border(1.dp, NoticeBorderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "💎 ASHA JEWELLERS ASSURANCE",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = NoticeTextColor,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• All gold jewellery sold is 100% BIS Hallmarked (916 HUID).\n• Daily gold bullion rate applied as per market standards.\n• Making charges include laser finishing, craftsmanship & certification.",
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color = NoticeTextColor
            )
        }
    }
}
