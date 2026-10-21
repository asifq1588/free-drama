package com.example.ui.screens.player

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.data.model.MonetagAdConfig
import com.example.ui.components.EpisodeGridItem
import com.example.ui.components.MonetagBannerCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReelsPlayerScreen(
    drama: Drama,
    episodes: List<Episode>,
    currentEpisodeIndex: Int,
    unlockedEpisodeIds: Set<String>,
    isVip: Boolean,
    monetagConfig: MonetagAdConfig,
    onBackClick: () -> Unit,
    onEpisodeSelect: (Int) -> Unit,
    onNextEpisode: () -> Unit,
    onPreviousEpisode: () -> Unit,
    onVipClick: () -> Unit,
    onAdClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentEpisode = episodes.getOrNull(currentEpisodeIndex) ?: episodes.firstOrNull()

    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var showEpisodeDrawer by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableIntStateOf(14200) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(90000L) }

    // ExoPlayer lifecycle management
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Update media source whenever episode changes
    LaunchedEffect(currentEpisode?.videoUrl) {
        currentEpisode?.let { ep ->
            try {
                val mediaItem = MediaItem.fromUri(Uri.parse(ep.videoUrl))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.setPlaybackSpeed(playbackSpeed)
                exoPlayer.play()
                isPlaying = true
            } catch (_: Exception) {
                // Fallback safe
            }
        }
    }

    // Progress polling
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPositionMs = exoPlayer.currentPosition
            durationMs = if (exoPlayer.duration > 0) exoPlayer.duration else 90000L
            delay(500)
        }
    }

    // Auto hide controls after 4 seconds
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000)
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .testTag("reels_player_screen")
    ) {
        // 9:16 Video Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
        )

        // Center Play / Pause Indicator (when toggled)
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(OledBlack.copy(alpha = 0.65f))
                    .align(Alignment.Center)
                    .clickable {
                        exoPlayer.play()
                        isPlaying = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = TextPrimary,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        // Top Bar Overlay
        AnimatedVisibility(
            visible = showControls,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(OledBlack.copy(alpha = 0.9f), Color.Transparent)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark.copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = drama.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Episode ${currentEpisode?.episodeNumber ?: 1} of ${episodes.size}",
                                fontSize = 11.sp,
                                color = AmberGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // VIP Upgrade Button
                    Surface(
                        color = if (isVip) AmberGold.copy(alpha = 0.2f) else FlameOrange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onVipClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isVip) "VIP Active" else "Go VIP",
                                color = AmberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Right Action Rail (Vertical TikTok / ReelShort Style)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        likesCount += if (isLiked) 1 else -1
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark.copy(alpha = 0.75f))
                        .testTag("like_button")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) NeonCrimson else TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "$likesCount",
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Episode Drawer Selector
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { showEpisodeDrawer = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark.copy(alpha = 0.75f))
                        .testTag("open_episodes_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = "All Episodes",
                        tint = AmberGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Episodes",
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Next Episode Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onNextEpisode,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark.copy(alpha = 0.75f))
                        .testTag("next_episode_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Episode",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Next",
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Playback Speed Toggle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        playbackSpeed = when (playbackSpeed) {
                            1.0f -> 1.25f
                            1.25f -> 1.5f
                            else -> 1.0f
                        }
                        exoPlayer.setPlaybackSpeed(playbackSpeed)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark.copy(alpha = 0.75f))
                ) {
                    Text(
                        text = "${playbackSpeed}x",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Speed",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }

        // Bottom Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, OledBlack.copy(alpha = 0.85f), OledBlack)
                        )
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Episode information
                Text(
                    text = "Episode ${currentEpisode?.episodeNumber}: ${currentEpisode?.title ?: ""}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = drama.synopsis,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                val progressFraction = if (durationMs > 0) {
                    (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Slider(
                    value = progressFraction,
                    onValueChange = { frac ->
                        val target = (frac * durationMs).toLong()
                        exoPlayer.seekTo(target)
                        currentPositionMs = target
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCrimson,
                        activeTrackColor = NeonCrimson,
                        inactiveTrackColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth().height(20.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentSec = (currentPositionMs / 1000).toInt()
                    val totalSec = (durationMs / 1000).toInt()
                    Text(
                        text = String.format("%02d:%02d", currentSec / 60, currentSec % 60),
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = String.format("%02d:%02d", totalSec / 60, totalSec % 60),
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Monetag Sponsored Bar
                MonetagBannerCard(
                    monetagConfig = monetagConfig,
                    customAdUrl = currentEpisode?.monetagAdUrl ?: "",
                    onAdClick = onAdClick
                )
            }
        }

        // Episode Selection Bottom Drawer
        if (showEpisodeDrawer) {
            ModalBottomSheet(
                onDismissRequest = { showEpisodeDrawer = false },
                containerColor = SurfaceElevated,
                contentColor = TextPrimary
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = drama.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Select Episode (${episodes.size} Total)",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(onClick = { showEpisodeDrawer = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp)
                    ) {
                        items(episodes) { ep ->
                            val isSelected = ep.episodeNumber == currentEpisode?.episodeNumber
                            val isUnlocked = isVip || unlockedEpisodeIds.contains(ep.id) || ep.accessType == com.example.data.model.EpisodeAccessType.FREE
                            EpisodeGridItem(
                                episode = ep,
                                isSelected = isSelected,
                                isUnlocked = isUnlocked,
                                onClick = {
                                    val index = episodes.indexOfFirst { it.id == ep.id }
                                    if (index != -1) {
                                        showEpisodeDrawer = false
                                        onEpisodeSelect(index)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
