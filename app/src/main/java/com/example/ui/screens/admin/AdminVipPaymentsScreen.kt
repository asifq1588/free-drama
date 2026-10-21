package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentGatewayConfig
import com.example.data.model.VipOrder
import com.example.data.model.VipOrderStatus
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminVipPaymentsScreen(
    paymentConfig: PaymentGatewayConfig,
    vipOrders: List<VipOrder>,
    onUpdatePaymentConfig: (PaymentGatewayConfig) -> Unit,
    onUpdateOrderStatus: (orderId: String, status: VipOrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPaymentActive by remember { mutableStateOf(paymentConfig.isPaymentActive) }
    var bkashNum by remember { mutableStateOf(paymentConfig.bKashNumber) }
    var nagadNum by remember { mutableStateOf(paymentConfig.nagadNumber) }
    var instructions by remember { mutableStateOf(paymentConfig.instructions) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(16.dp)
            .testTag("admin_vip_payments_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "bKash & Nagad VIP Gateway",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Configure payment numbers & approve VIP subscriptions",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = if (isPaymentActive) EmeraldGreen.copy(alpha = 0.2f) else NeonCrimson.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isPaymentActive) "GATEWAY ACTIVE" else "DISABLED",
                        color = if (isPaymentActive) EmeraldGreen else NeonCrimson,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Gateway Settings Card
        item {
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Payment Gateway Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Switch(
                            checked = isPaymentActive,
                            onCheckedChange = { isPaymentActive = it }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = bkashNum,
                            onValueChange = { bkashNum = it },
                            label = { Text("bKash Mobile Number") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = nagadNum,
                            onValueChange = { nagadNum = it },
                            label = { Text("Nagad Mobile Number") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Payment Instructions for Users") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onUpdatePaymentConfig(
                                paymentConfig.copy(
                                    isPaymentActive = isPaymentActive,
                                    bKashNumber = bkashNum,
                                    nagadNumber = nagadNum,
                                    instructions = instructions
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Save Payment Settings",
                            color = OledBlack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Pending & Recent Orders Section
        item {
            Text(
                text = "User Payment Requests & Order History (${vipOrders.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }

        if (vipOrders.isEmpty()) {
            item {
                Text(
                    text = "No payment orders received yet.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        } else {
            items(vipOrders) { order ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(order.timestamp))
                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (order.paymentMethod == "bKash") Color(0xFFE2136E) else Color(0xFFF7941D),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = order.paymentMethod,
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = order.senderPhone,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }

                            // Status badge
                            val (badgeBg, badgeColor, label) = when (order.status) {
                                VipOrderStatus.PENDING -> Triple(TagAdBg, FlameOrange, "PENDING")
                                VipOrderStatus.APPROVED -> Triple(TagFreeBg, EmeraldGreen, "APPROVED")
                                VipOrderStatus.REJECTED -> Triple(SurfaceBorder, TextMuted, "REJECTED")
                            }

                            Surface(color = badgeBg, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = label,
                                    color = badgeColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TrxID: ${order.trxId}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AmberGold
                            )
                            Text(
                                text = "${order.planName} • ${order.amount}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = dateStr,
                            fontSize = 10.sp,
                            color = TextMuted
                        )

                        if (order.status == VipOrderStatus.PENDING) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onUpdateOrderStatus(order.id, VipOrderStatus.REJECTED) },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = NeonCrimson, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reject", fontSize = 11.sp, color = NeonCrimson)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { onUpdateOrderStatus(order.id, VipOrderStatus.APPROVED) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("approve_order_${order.id}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = OledBlack, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve VIP", fontSize = 11.sp, color = OledBlack, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
