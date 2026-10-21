package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Episode
import com.example.data.model.EpisodeAccessType
import com.example.ui.theme.*

@Composable
fun EpisodeGridItem(
    episode: Episode,
    isSelected: Boolean,
    isUnlocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> NeonCrimson.copy(alpha = 0.25f)
        !isUnlocked -> SurfaceDark
        else -> SurfaceElevated
    }

    val borderColor = when {
        isSelected -> NeonCrimson
        !isUnlocked -> SurfaceBorder
        else -> SurfaceHighlight
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("episode_item_${episode.episodeNumber}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${episode.episodeNumber}",
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isSelected) NeonCrimson else TextPrimary
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(2.dp))
                Icon(
                    imageVector = Icons.Default.Equalizer,
                    contentDescription = "Playing",
                    tint = NeonCrimson,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Top right indicator badge
        if (!isUnlocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
            ) {
                when (episode.accessType) {
                    EpisodeAccessType.AD_REQUIRED -> {
                        Surface(
                            color = FlameOrange,
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = "AD",
                                color = TextPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                            )
                        }
                    }
                    EpisodeAccessType.VIP_ONLY -> {
                        Surface(
                            color = AmberGold,
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "VIP Locked",
                                tint = OledBlack,
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(1.dp)
                            )
                        }
                    }
                    EpisodeAccessType.FREE -> {}
                }
            }
        }
    }
}
