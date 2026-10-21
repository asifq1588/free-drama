package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.data.model.EpisodeAccessType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEpisodesScreen(
    dramas: List<Drama>,
    episodesMap: Map<String, List<Episode>>,
    onAddEpisode: (dramaId: String, episode: Episode) -> Unit,
    onUpdateEpisode: (dramaId: String, episode: Episode) -> Unit,
    onDeleteEpisode: (dramaId: String, episodeId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDramaId by remember { mutableStateOf(dramas.firstOrNull()?.id ?: "") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingEpisode by remember { mutableStateOf<Episode?>(null) }

    val currentEpisodes = episodesMap[selectedDramaId] ?: emptyList()
    val selectedDrama = dramas.find { it.id == selectedDramaId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(16.dp)
            .testTag("admin_episodes_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Episodes & Monetag Manager",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Configure each episode & assign individual Monetag ads",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                shape = RoundedCornerShape(8.dp),
                enabled = selectedDramaId.isNotBlank(),
                modifier = Modifier.testTag("add_episode_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Episode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Drama Selector Chips
        Text(
            text = "Select Mini-Series to Manage:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(dramas) { drama ->
                val isSelected = drama.id == selectedDramaId
                Surface(
                    color = if (isSelected) NeonCrimson else SurfaceDark,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedDramaId = drama.id }
                        .testTag("drama_select_chip_${drama.id}")
                ) {
                    Text(
                        text = drama.title,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Drama Stats Summary Bar
        selectedDrama?.let { drama ->
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Added", fontSize = 10.sp, color = TextMuted)
                        Text("${currentEpisodes.size}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Free Ep", fontSize = 10.sp, color = TextMuted)
                        Text(
                            "${currentEpisodes.count { it.accessType == EpisodeAccessType.FREE }}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EmeraldGreen
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ad Required", fontSize = 10.sp, color = TextMuted)
                        Text(
                            "${currentEpisodes.count { it.accessType == EpisodeAccessType.AD_REQUIRED }}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = FlameOrange
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("VIP Only", fontSize = 10.sp, color = TextMuted)
                        Text(
                            "${currentEpisodes.count { it.accessType == EpisodeAccessType.VIP_ONLY }}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AmberGold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Episode List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            items(currentEpisodes) { ep ->
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
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceHighlight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${ep.episodeNumber}",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = ep.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${ep.durationSeconds}s duration",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            // Access Badge
                            val (badgeBg, badgeText, badgeLabel) = when (ep.accessType) {
                                EpisodeAccessType.FREE -> Triple(TagFreeBg, TagFreeText, "FREE")
                                EpisodeAccessType.AD_REQUIRED -> Triple(TagAdBg, TagAdText, "MONETAG AD")
                                EpisodeAccessType.VIP_ONLY -> Triple(TagVipBg, TagVipText, "VIP ONLY")
                            }

                            Surface(
                                color = badgeBg,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = badgeLabel,
                                    color = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Video URL
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = ep.videoUrl,
                                fontSize = 10.sp,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Monetag Ad Tag info
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = FlameOrange,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (ep.monetagAdUrl.isNotBlank()) "Monetag Ad Tag: ${ep.monetagAdUrl}" else "Using Global Monetag Ad Setup",
                                fontSize = 10.sp,
                                color = if (ep.monetagAdUrl.isNotBlank()) FlameOrange else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action buttons row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Quick toggle access type
                            TextButton(
                                onClick = {
                                    val nextType = when (ep.accessType) {
                                        EpisodeAccessType.FREE -> EpisodeAccessType.AD_REQUIRED
                                        EpisodeAccessType.AD_REQUIRED -> EpisodeAccessType.VIP_ONLY
                                        EpisodeAccessType.VIP_ONLY -> EpisodeAccessType.FREE
                                    }
                                    onUpdateEpisode(selectedDramaId, ep.copy(accessType = nextType))
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Toggle Tier", fontSize = 11.sp, color = AmberGold)
                            }

                            // Edit button
                            TextButton(
                                onClick = { editingEpisode = ep },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Edit Ad/Video", fontSize = 11.sp, color = TextPrimary)
                            }

                            // Delete button
                            IconButton(
                                onClick = { onDeleteEpisode(selectedDramaId, ep.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonCrimson, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add or Edit Episode Dialog
    if (showAddDialog || editingEpisode != null) {
        val epToEdit = editingEpisode
        var epNumber by remember { mutableIntStateOf(epToEdit?.episodeNumber ?: ((currentEpisodes.maxOfOrNull { it.episodeNumber } ?: 0) + 1)) }
        var title by remember { mutableStateOf(epToEdit?.title ?: "Episode $epNumber") }
        var videoUrl by remember {
            mutableStateOf(
                epToEdit?.videoUrl
                    ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            )
        }
        var accessType by remember { mutableStateOf(epToEdit?.accessType ?: EpisodeAccessType.AD_REQUIRED) }
        var monetagAdUrl by remember {
            mutableStateOf(
                epToEdit?.monetagAdUrl ?: "https://otieuwew.net/4/8592147?ep=$epNumber"
            )
        }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingEpisode = null
            },
            title = {
                Text(
                    text = if (epToEdit != null) "Edit Episode $epNumber & Monetag Ad" else "Add New Episode to Series",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = "$epNumber",
                            onValueChange = { epNumber = it.toIntOrNull() ?: epNumber },
                            label = { Text("Episode #") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Episode Title") },
                            modifier = Modifier.weight(2f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text("Video URL (HLS / MP4)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = monetagAdUrl,
                        onValueChange = { monetagAdUrl = it },
                        label = { Text("Monetag Ad Direct Link / Tag for this Ep") },
                        placeholder = { Text("https://otieuwew.net/4/...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = "Access Requirement:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            EpisodeAccessType.FREE to "Free",
                            EpisodeAccessType.AD_REQUIRED to "Monetag Ad",
                            EpisodeAccessType.VIP_ONLY to "VIP Only"
                        ).forEach { (type, label) ->
                            val isSelected = accessType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { accessType = type },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (type == EpisodeAccessType.FREE) EmeraldGreen else if (type == EpisodeAccessType.AD_REQUIRED) FlameOrange else AmberGold,
                                    selectedLabelColor = if (type == EpisodeAccessType.FREE) OledBlack else TextPrimary
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalEp = Episode(
                            id = epToEdit?.id ?: "${selectedDramaId}_ep_$epNumber",
                            dramaId = selectedDramaId,
                            episodeNumber = epNumber,
                            title = title,
                            videoUrl = videoUrl,
                            accessType = accessType,
                            monetagAdUrl = monetagAdUrl,
                            isUnlocked = accessType == EpisodeAccessType.FREE
                        )
                        if (epToEdit != null) {
                            onUpdateEpisode(selectedDramaId, finalEp)
                        } else {
                            onAddEpisode(selectedDramaId, finalEp)
                        }
                        showAddDialog = false
                        editingEpisode = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson)
                ) {
                    Text("Save Episode")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        editingEpisode = null
                    }
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceElevated
        )
    }
}
