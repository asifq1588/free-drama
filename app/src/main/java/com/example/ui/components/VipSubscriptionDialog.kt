package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PaymentGatewayConfig
import com.example.data.model.VipPlan
import com.example.ui.theme.*

@Composable
fun VipSubscriptionDialog(
    vipPlans: List<VipPlan>,
    paymentConfig: PaymentGatewayConfig,
    onSubmitOrder: (phone: String, trxId: String, method: String, plan: VipPlan) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedPlanId by remember { mutableStateOf(vipPlans.find { it.isPopular }?.id ?: vipPlans.firstOrNull()?.id ?: "") }
    var selectedPaymentMethod by remember { mutableStateOf("bKash") }
    var senderPhone by remember { mutableStateOf("") }
    var transactionId by remember { mutableStateOf("") }

    val activePlan = vipPlans.find { it.id == selectedPlanId } ?: vipPlans.first()
    val sendMoneyNumber = if (selectedPaymentMethod == "bKash") paymentConfig.bKashNumber else paymentConfig.nagadNumber

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OledBlack.copy(alpha = 0.88f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(24.dp),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("vip_subscription_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(AmberGold, FlameOrange)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "VIP",
                                    tint = OledBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Premium VIP Subscription",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Ad-free full HD drama & all episodes unlocked",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Step 1: Select Plan
                    Text(
                        text = "1. Select Plan (Choose VIP Tier):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        vipPlans.forEach { plan ->
                            val isSelected = plan.id == selectedPlanId
                            Surface(
                                color = if (isSelected) AmberGold.copy(alpha = 0.12f) else SurfaceDark,
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) ButtonDefaults.outlinedButtonBorder else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) AmberGold else SurfaceBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedPlanId = plan.id }
                                    .padding(8.dp)
                                    .testTag("vip_plan_card_${plan.id}")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (plan.isPopular) {
                                        Surface(
                                            color = FlameOrange,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "POPULAR",
                                                color = TextPrimary,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    } else if (plan.isBestValue) {
                                        Surface(
                                            color = AmberGold,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "BEST VALUE",
                                                color = OledBlack,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    } else {
                                        Spacer(modifier = Modifier.height(14.dp))
                                    }

                                    Text(
                                        text = plan.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = plan.price,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = AmberGold
                                    )
                                    Text(
                                        text = "/ ${plan.durationLabel}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    plan.features.take(2).forEach { feat ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = EmeraldGreen,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = feat,
                                                fontSize = 8.sp,
                                                color = TextSecondary,
                                                lineHeight = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Step 2: Payment Method
                    Text(
                        text = "2. Select Payment Method:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // bKash Button
                        val isBkash = selectedPaymentMethod == "bKash"
                        Surface(
                            color = if (isBkash) Color(0xFFE2136E).copy(alpha = 0.18f) else SurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    if (isBkash) 1.5.dp else 1.dp,
                                    if (isBkash) Color(0xFFE2136E) else SurfaceBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPaymentMethod = "bKash" }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("payment_method_bkash")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE2136E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "bK",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "bKash",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Personal Send Money",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }

                        // Nagad Button
                        val isNagad = selectedPaymentMethod == "Nagad"
                        Surface(
                            color = if (isNagad) Color(0xFFF7941D).copy(alpha = 0.18f) else SurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    if (isNagad) 1.5.dp else 1.dp,
                                    if (isNagad) Color(0xFFF7941D) else SurfaceBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPaymentMethod = "Nagad" }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("payment_method_nagad")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF7941D)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "N",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Nagad",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Personal Send Money",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Send Money Box with Copy Button
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$selectedPaymentMethod Number (Send Money):",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Surface(
                                    color = AmberGold.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Personal",
                                        color = AmberGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sendMoneyNumber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )

                                Button(
                                    onClick = { copyToClipboard(sendMoneyNumber, "$selectedPaymentMethod Number") },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceHighlight),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("copy_number_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Number",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Copy",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = paymentConfig.instructions,
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Step 3: Payment Verification
                    Text(
                        text = "3. Payment Verification:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = senderPhone,
                        onValueChange = { senderPhone = it },
                        label = { Text("Sender Mobile Number") },
                        placeholder = { Text("e.g. 017XXXXXXXX") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = AmberGold,
                            unfocusedLabelColor = TextMuted
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sender_phone_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = transactionId,
                        onValueChange = { transactionId = it },
                        label = { Text("Transaction ID (TrxID)") },
                        placeholder = { Text("e.g. BL98K23N09") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = AmberGold,
                            unfocusedLabelColor = TextMuted
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trx_id_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit CTA
                    Button(
                        onClick = {
                            onSubmitOrder(senderPhone, transactionId, selectedPaymentMethod, activePlan)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_vip_order_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = null,
                            tint = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pay ${activePlan.price} & Activate VIP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}
