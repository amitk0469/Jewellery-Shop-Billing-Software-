package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CurrencyHelper
import com.example.ui.BillingUiState
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoicePrintDialog(
    state: BillingUiState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color(0x33000000), RoundedCornerShape(24.dp)),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "💎",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ASHA JEWELLERS",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = GoldDeep,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "TAX INVOICE / CASH MEMO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CharcoalDark
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0x1F000000))

                // Scrollable Bill Content
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                ) {
                    // Invoice metadata
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Customer:", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = state.customerInfo.name.ifBlank { "Cash Customer" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CharcoalDark
                            )
                            if (state.customerInfo.phone.isNotBlank()) {
                                Text(
                                    text = "Ph: ${state.customerInfo.phone}",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Invoice #${state.customerInfo.invoiceNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GoldDeep
                            )
                            Text(text = dateStr, fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = "Gold Rate: ${CurrencyHelper.formatInr(state.goldPricePerGram)}/g",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CharcoalDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Items Table
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8F9FA))
                            .border(1.dp, Color(0x14000000), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Item Details", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1.5f))
                                Text("Wt (g)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(0.9f))
                                Text("Making", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(0.9f))
                                Text("Total", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1.1f))
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0x1A000000))

                            state.items.forEachIndexed { idx, item ->
                                val itemGoldVal = item.calculateGoldValue(state.goldPricePerGram)
                                val itemMaking = item.calculateMakingAmount(state.goldPricePerGram)
                                val itemTotal = item.calculateTotalAmount(state.goldPricePerGram)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1.5f)) {
                                        Text(item.name.ifBlank { "Item ${idx + 1}" }, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        if (item.otherCharges > 0) {
                                            Text("+ Other: ₹${item.otherCharges.toInt()}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                    Text(String.format(Locale.US, "%.3fg", item.weightGrams), fontSize = 12.sp, modifier = Modifier.weight(0.9f))
                                    Text(CurrencyHelper.formatInr(itemMaking), fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.weight(0.9f))
                                    Text(CurrencyHelper.formatInr(itemTotal), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CharcoalDark, modifier = Modifier.weight(1.1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calculation Summary
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BillSummaryRow("Total Gold Weight", CurrencyHelper.formatGrams(state.totalWeightGrams))
                        BillSummaryRow("Total Gold Value", CurrencyHelper.formatInr(state.totalGoldValue))
                        BillSummaryRow("Total Making Charges", CurrencyHelper.formatInr(state.totalMakingCharges))
                        if (state.totalOtherCharges > 0) {
                            BillSummaryRow("Other Charges", CurrencyHelper.formatInr(state.totalOtherCharges))
                        }
                        BillSummaryRow("Gross Amount", CurrencyHelper.formatInr(state.grossAmount), isBold = true)

                        if (state.applyGst) {
                            BillSummaryRow("GST (${state.gstPercent}%)", CurrencyHelper.formatInr(state.gstAmount))
                        }

                        if (state.oldGoldDeduction > 0) {
                            BillSummaryRow(
                                "Old Gold Scrap (${state.oldGold.weightGrams}g @ ₹${state.oldGold.ratePerGram.toInt()})",
                                "- ${CurrencyHelper.formatInr(state.oldGoldDeduction)}",
                                valueColor = Color(0xFFB52D2D)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0x26000000))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9F5EC), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0x33C79A45), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("NET PAYABLE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                                Text("INCL. ALL TAXES", fontSize = 9.sp, color = Color.Gray)
                            }
                            Text(
                                text = CurrencyHelper.formatInr(state.netPayable),
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = GoldDeep
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Authenticity & Hallmarking Seal
                    Text(
                        text = "⭐ 100% BIS Hallmarked 916 Jewellery with HUID guarantee.\nThank you for choosing Asha Jewellers!",
                        fontSize = 10.sp,
                        color = Color(0xFF7A6538),
                        lineHeight = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFBEA), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Share & Print
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { shareInvoiceText(context, state, dateStr) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { printInvoice(context, state, dateStr) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BillSummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = CharcoalDark
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isBold) 13.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) CharcoalDark else Color(0xFF555555)
        )
        Text(
            text = value,
            fontSize = if (isBold) 13.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = valueColor
        )
    }
}

private fun shareInvoiceText(context: Context, state: BillingUiState, dateStr: String) {
    val itemsSummary = state.items.mapIndexed { idx, it ->
        val total = it.calculateTotalAmount(state.goldPricePerGram)
        "${idx + 1}. ${it.name} - ${it.weightGrams}g: ${CurrencyHelper.formatInr(total)}"
    }.joinToString("\n")

    val text = """
        ✨ ASHA JEWELLERS - TAX INVOICE ✨
        ---------------------------------
        Invoice No: #${state.customerInfo.invoiceNumber}
        Date: $dateStr
        Customer: ${state.customerInfo.name.ifBlank { "Cash Customer" }}
        Phone: ${state.customerInfo.phone}
        Gold Rate: ${CurrencyHelper.formatInr(state.goldPricePerGram)}/g
        ---------------------------------
        ITEMS:
        $itemsSummary
        ---------------------------------
        Total Weight: ${CurrencyHelper.formatGrams(state.totalWeightGrams)}
        Gold Value: ${CurrencyHelper.formatInr(state.totalGoldValue)}
        Making Charges: ${CurrencyHelper.formatInr(state.totalMakingCharges)}
        ${if (state.totalOtherCharges > 0) "Other Charges: " + CurrencyHelper.formatInr(state.totalOtherCharges) + "\n" else ""}Gross Amount: ${CurrencyHelper.formatInr(state.grossAmount)}
        ${if (state.applyGst) "GST (" + state.gstPercent + "%): " + CurrencyHelper.formatInr(state.gstAmount) + "\n" else ""}${if (state.oldGoldDeduction > 0) "Old Gold Scrap: - " + CurrencyHelper.formatInr(state.oldGoldDeduction) + "\n" else ""}---------------------------------
        💎 NET PAYABLE: ${CurrencyHelper.formatInr(state.netPayable)}
        ---------------------------------
        100% BIS Hallmarked 916 Jewellery with HUID.
        Thank you for your visit!
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Asha Jewellers Bill #${state.customerInfo.invoiceNumber}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Invoice via"))
}

private fun printInvoice(context: Context, state: BillingUiState, dateStr: String) {
    try {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return

        val itemsHtml = state.items.mapIndexed { idx, item ->
            val gVal = item.calculateGoldValue(state.goldPricePerGram)
            val mVal = item.calculateMakingAmount(state.goldPricePerGram)
            val total = item.calculateTotalAmount(state.goldPricePerGram)
            """
            <tr>
                <td style="padding:6px;border-bottom:1px solid #ddd;">${idx + 1}. ${item.name}</td>
                <td style="padding:6px;border-bottom:1px solid #ddd;text-align:right;">${item.weightGrams}g</td>
                <td style="padding:6px;border-bottom:1px solid #ddd;text-align:right;">₹${gVal.toInt()}</td>
                <td style="padding:6px;border-bottom:1px solid #ddd;text-align:right;">₹${mVal.toInt()}</td>
                <td style="padding:6px;border-bottom:1px solid #ddd;text-align:right;"><b>₹${total.toInt()}</b></td>
            </tr>
            """.trimIndent()
        }.joinToString("\n")

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; margin: 20px; color: #171717; }
                    .header { text-align: center; border-bottom: 2px solid #8D641F; padding-bottom: 10px; margin-bottom: 15px; }
                    .header h1 { color: #8D641F; margin: 0; font-size: 24px; letter-spacing: 2px; }
                    .meta { display: flex; justify-content: space-between; margin-bottom: 15px; font-size: 13px; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 15px; font-size: 13px; }
                    th { background: #f4eee1; padding: 8px; text-align: left; }
                    .summary { width: 100%; margin-top: 10px; font-size: 13px; }
                    .summary td { padding: 4px; }
                    .total-box { background: #fdf8ed; border: 1px solid #c79a45; padding: 12px; margin-top: 15px; text-align: right; }
                    .footer { text-align: center; margin-top: 25px; font-size: 11px; color: #666; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>💎 ASHA JEWELLERS</h1>
                    <div style="font-size:12px;color:#666;margin-top:4px;">GOLD JEWELLERY BILLING INVOICE</div>
                </div>
                <div class="meta">
                    <div>
                        <b>Customer:</b> ${state.customerInfo.name.ifBlank { "Cash Customer" }}<br>
                        ${if (state.customerInfo.phone.isNotBlank()) "<b>Phone:</b> " + state.customerInfo.phone + "<br>" else ""}
                    </div>
                    <div style="text-align:right;">
                        <b>Invoice:</b> #${state.customerInfo.invoiceNumber}<br>
                        <b>Date:</b> $dateStr<br>
                        <b>Gold Rate:</b> ₹${state.goldPricePerGram.toInt()}/g
                    </div>
                </div>
                <table>
                    <thead>
                        <tr>
                            <th>Item</th>
                            <th style="text-align:right;">Weight</th>
                            <th style="text-align:right;">Gold Val</th>
                            <th style="text-align:right;">Making</th>
                            <th style="text-align:right;">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        $itemsHtml
                    </tbody>
                </table>
                <table class="summary">
                    <tr><td>Total Gold Weight:</td><td style="text-align:right;"><b>${state.totalWeightGrams} g</b></td></tr>
                    <tr><td>Gross Amount:</td><td style="text-align:right;">₹${state.grossAmount.toInt()}</td></tr>
                    ${if (state.applyGst) "<tr><td>GST (" + state.gstPercent + "%):</td><td style=\"text-align:right;\">₹" + state.gstAmount.toInt() + "</td></tr>" else ""}
                    ${if (state.oldGoldDeduction > 0) "<tr><td style=\"color:red;\">Old Gold Scrap:</td><td style=\"text-align:right;color:red;\">- ₹" + state.oldGoldDeduction.toInt() + "</td></tr>" else ""}
                </table>
                <div class="total-box">
                    <span style="font-size:14px;color:#555;">NET PAYABLE AMOUNT: </span>
                    <b style="font-size:22px;color:#8D641F;">${CurrencyHelper.formatInr(state.netPayable)}</b>
                </div>
                <div class="footer">
                    100% BIS Hallmarked 916 Jewellery with HUID Guarantee.<br>
                    Thank you for shopping at Asha Jewellers!
                </div>
            </body>
            </html>
        """.trimIndent()

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Asha_Jewellers_Invoice_${state.customerInfo.invoiceNumber}")
                printManager.print("Asha Jewellers Bill", printAdapter, PrintAttributes.Builder().build())
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
    } catch (_: Exception) {
    }
}
