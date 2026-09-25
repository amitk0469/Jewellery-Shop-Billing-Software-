package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InvoiceEntity
import com.example.model.CurrencyHelper
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GrayText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    invoices: List<InvoiceEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLoadInvoice: (InvoiceEntity) -> Unit,
    onDeleteInvoice: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF9F7F2),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GoldBadge(text = "📜", size = 38.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Saved Invoices",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                        Text(
                            text = "${invoices.size} records in history",
                            fontSize = 12.sp,
                            color = GrayText
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = CharcoalDark)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by customer, phone, or invoice #", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldDeep)
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xCCFFFFFF),
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0x1F000000)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Invoices List
            if (invoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💎", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No invoices match '$searchQuery'"
                            else "No saved invoices yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CharcoalDark
                        )
                        Text(
                            text = "Save completed bills to easily recall or reprint later",
                            fontSize = 12.sp,
                            color = GrayText,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(invoices, key = { it.id }) { invoice ->
                        InvoiceHistoryItem(
                            invoice = invoice,
                            onLoad = { onLoadInvoice(invoice) },
                            onDelete = { onDeleteInvoice(invoice.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceHistoryItem(
    invoice: InvoiceEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        .format(Date(invoice.dateTimestamp))

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Color(0x14000000), shape)
            .clickable(onClick = onLoad)
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = invoice.customerName.ifBlank { "Cash Customer" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CharcoalDark
                    )
                    Text(
                        text = "Invoice #${invoice.invoiceNumber}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = GoldDeep
                    )
                }

                Text(
                    text = CurrencyHelper.formatInr(invoice.netPayable),
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = GoldDeep
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0x0D000000))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weight: ${CurrencyHelper.formatGrams(invoice.totalWeightGrams)} • Gold @ ${CurrencyHelper.formatInr(invoice.goldRatePerGram)}/g",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                    Text(
                        text = dateStr,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Row {
                    IconButton(
                        onClick = onLoad,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = "Load bill",
                            tint = GoldDeep,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete bill",
                            tint = Color(0xFFB52D2D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
