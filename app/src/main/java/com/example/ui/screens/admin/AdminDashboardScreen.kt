package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.AdminSubTab
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    currentTab: AdminSubTab,
    onTabChange: (AdminSubTab) -> Unit,
    onExitAdminMode: () -> Unit,
    dramas: List<Drama>,
    episodesMap: Map<String, List<Episode>>,
    vipOrders: List<VipOrder>,
    paymentConfig: PaymentGatewayConfig,
    monetagConfig: MonetagAdConfig,
    currentUser: UserAccount,
    onToggleUserVip: (Boolean) -> Unit,
    onAddDrama: (Drama) -> Unit,
    onUpdateDrama: (Drama) -> Unit,
    onDeleteDrama: (String) -> Unit,
    onAddEpisode: (String, Episode) -> Unit,
    onUpdateEpisode: (String, Episode) -> Unit,
    onDeleteEpisode: (String, String) -> Unit,
    onUpdatePaymentConfig: (PaymentGatewayConfig) -> Unit,
    onUpdateOrderStatus: (String, VipOrderStatus) -> Unit,
    onUpdateMonetagConfig: (MonetagAdConfig) -> Unit,
    isSyncingFirebase: Boolean = false,
    onSyncFirebaseClick: () -> Unit = {},
    htmlCode: String = "",
    onDownloadHtmlClick: (android.content.Context, (java.io.File?) -> Unit) -> Unit = { _, _ -> },
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalEpisodes = episodesMap.values.sumOf { it.size }
    val pendingOrdersCount = vipOrders.count { it.status == VipOrderStatus.PENDING }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        // Admin Top Bar
        Surface(
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = NeonCrimson,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ADMIN",
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DashReels Control Panel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Exit to User Mode
                        Button(
                            onClick = onExitAdminMode,
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceHighlight),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("exit_admin_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "User Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                        }

                        // Logout Admin
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCrimson.copy(alpha = 0.2f))
                                .testTag("logout_admin_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock & Logout Admin",
                                tint = NeonCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SubTab Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AdminSubTab.values()) { tab ->
                        val isSelected = tab == currentTab
                        Surface(
                            color = if (isSelected) NeonCrimson else SurfaceElevated,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onTabChange(tab) }
                                .testTag("admin_subtab_${tab.name}")
                        ) {
                            Text(
                                text = tab.title,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Body Content based on SubTab
        when (currentTab) {
            AdminSubTab.DASHBOARD -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick stats grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = SurfaceDark,
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Total Dramas", fontSize = 10.sp, color = TextMuted)
                                Text("${dramas.size}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                        }
                        Surface(
                            color = SurfaceDark,
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Total Episodes", fontSize = 10.sp, color = TextMuted)
                                Text("$totalEpisodes", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NeonCrimson)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = SurfaceDark,
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Pending Orders", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    "$pendingOrdersCount",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (pendingOrdersCount > 0) FlameOrange else EmeraldGreen
                                )
                            }
                        }
                        Surface(
                            color = SurfaceDark,
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Monetag Views", fontSize = 10.sp, color = TextMuted)
                                Text("${monetagConfig.impressionsCount}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AmberGold)
                            }
                        }
                    }

                    // User Panel Testing Card
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
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("User Testing & Account Controls:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Toggle User VIP Access", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text("Unlocks all episodes instantly for live preview", fontSize = 10.sp, color = TextMuted)
                                }
                                Switch(
                                    checked = currentUser.isVip,
                                    onCheckedChange = { onToggleUserVip(it) }
                                )
                            }
                        }
                    }

                    // Quick Jump cards
                    Text("Quick Management Actions:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onTabChange(AdminSubTab.EPISODES) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Manage Episodes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onTabChange(AdminSubTab.VIP_PAYMENTS) },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Verify Payments", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OledBlack)
                        }
                    }
                }
            }
            AdminSubTab.DRAMAS -> {
                AdminDramasScreen(
                    dramas = dramas,
                    onAddDrama = onAddDrama,
                    onUpdateDrama = onUpdateDrama,
                    onDeleteDrama = onDeleteDrama
                )
            }
            AdminSubTab.EPISODES -> {
                AdminEpisodesScreen(
                    dramas = dramas,
                    episodesMap = episodesMap,
                    onAddEpisode = onAddEpisode,
                    onUpdateEpisode = onUpdateEpisode,
                    onDeleteEpisode = onDeleteEpisode
                )
            }
            AdminSubTab.VIP_PAYMENTS -> {
                AdminVipPaymentsScreen(
                    paymentConfig = paymentConfig,
                    vipOrders = vipOrders,
                    onUpdatePaymentConfig = onUpdatePaymentConfig,
                    onUpdateOrderStatus = onUpdateOrderStatus
                )
            }
            AdminSubTab.MONETAG_ADS -> {
                AdminAdsSetupScreen(
                    monetagConfig = monetagConfig,
                    onUpdateConfig = onUpdateMonetagConfig
                )
            }
            AdminSubTab.FIREBASE_SETUP -> {
                AdminFirebaseSetupScreen(
                    dramas = dramas,
                    episodesMap = episodesMap,
                    isSyncing = isSyncingFirebase,
                    onSyncNowClick = onSyncFirebaseClick
                )
            }
            AdminSubTab.HTML_EXPORT -> {
                AdminHtmlExportScreen(
                    htmlCode = htmlCode,
                    onDownloadClick = onDownloadHtmlClick
                )
            }
        }
    }
}

