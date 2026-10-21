package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.data.model.MonetagAdConfig
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AdUnlockDialog(
    drama: Drama,
    episode: Episode,
    monetagConfig: MonetagAdConfig,
    onUnlockComplete: () -> Unit,
    onUpgradeVipClick: () -> Unit,
    onDismiss: () -> Unit
) {
    var isWatchingAd by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(5) }

    LaunchedEffect(isWatchingAd) {
        if (isWatchingAd) {
            countdown = monetagConfig.adDurationSeconds.coerceAtLeast(3)
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            // Finished watching Monetag ad
            onUnlockComplete()
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isWatchingAd) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OledBlack.copy(alpha = 0.85f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(20.dp),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("ad_unlock_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { if (!isWatchingAd) onDismiss() },
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

                    // Ad icon badge
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(FlameOrange, NeonCrimson)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Ad Reward",
                                tint = TextPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "AD UNLOCK",
                                color = TextPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Watch Ad to Unlock Episode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This episode requires watching a short sponsor ad to unlock. Watch the ad to enjoy free streaming!",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Episode badge
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "🎬 ${drama.title} • Episode ${episode.episodeNumber}",
                            color = AmberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isWatchingAd) {
                        // Watching Ad animation & progress
                        Surface(
                            color = SurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = FlameOrange,
                                    modifier = Modifier.size(36.dp),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Playing Monetag Sponsored Ad...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Unlocking in $countdown seconds",
                                    fontSize = 12.sp,
                                    color = FlameOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Primary CTA: Watch Ad
                        Button(
                            onClick = { isWatchingAd = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("unlock_with_ad_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Unlock with Ad (Watch Ad)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Secondary CTA: Upgrade to VIP
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onUpgradeVipClick()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("upgrade_vip_from_ad_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upgrade to Premium VIP (Ad-Free)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AmberGold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Back to Episode List",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onDismiss() }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Monetag sponsor bar
                    MonetagBannerCard(
                        monetagConfig = monetagConfig,
                        customAdUrl = episode.monetagAdUrl
                    )
                }
            }
        }
    }
}
