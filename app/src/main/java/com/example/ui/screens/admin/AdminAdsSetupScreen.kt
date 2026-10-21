package com.example.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonetagAdConfig
import com.example.ui.theme.*

@Composable
fun AdminAdsSetupScreen(
    monetagConfig: MonetagAdConfig,
    onUpdateConfig: (MonetagAdConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(monetagConfig.isEnabled) }
    var directLink by remember { mutableStateOf(monetagConfig.globalDirectLinkUrl) }
    var bannerZone by remember { mutableStateOf(monetagConfig.bannerZoneId) }
    var rewardedZone by remember { mutableStateOf(monetagConfig.rewardedZoneId) }
    var adDuration by remember { mutableIntStateOf(monetagConfig.adDurationSeconds) }
    var freq by remember { mutableIntStateOf(monetagConfig.interstitialFrequency) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 90.dp)
            .testTag("admin_ads_setup_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Monetag Ad Monetization",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Direct Links, Interstitial, and In-Stream Video Ads",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Surface(
                color = if (isEnabled) FlameOrange.copy(alpha = 0.2f) else SurfaceBorder,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isEnabled) "ADS ACTIVE" else "PAUSED",
                    color = if (isEnabled) FlameOrange else TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Live Analytics Row
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
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Impressions", fontSize = 10.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${monetagConfig.impressionsCount}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
            }
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Ad Clicks", fontSize = 10.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${monetagConfig.clicksCount}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = FlameOrange)
                }
            }
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Est. Revenue", fontSize = 10.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    val est = (monetagConfig.impressionsCount * 0.003f) + (monetagConfig.clicksCount * 0.035f)
                    Text("$" + String.format("%.2f", est), fontSize = 15.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
                }
            }
        }

        // Configuration Form
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Monetag Ads Network",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it }
                    )
                }

                OutlinedTextField(
                    value = directLink,
                    onValueChange = { directLink = it },
                    label = { Text("Global Monetag Direct Link URL") },
                    placeholder = { Text("https://otieuwew.net/4/...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = bannerZone,
                        onValueChange = { bannerZone = it },
                        label = { Text("Banner Zone ID") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rewardedZone,
                        onValueChange = { rewardedZone = it },
                        label = { Text("Rewarded Zone ID") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = "$adDuration",
                        onValueChange = { adDuration = it.toIntOrNull() ?: adDuration },
                        label = { Text("Ad Watch Timer (sec)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = "$freq",
                        onValueChange = { freq = it.toIntOrNull() ?: freq },
                        label = { Text("Frequency (Every X Ep)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(directLink))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Direct Link", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onUpdateConfig(
                                monetagConfig.copy(
                                    isEnabled = isEnabled,
                                    globalDirectLinkUrl = directLink,
                                    bannerZoneId = bannerZone,
                                    rewardedZoneId = rewardedZone,
                                    adDurationSeconds = adDuration,
                                    interstitialFrequency = freq
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Ad Setup", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
