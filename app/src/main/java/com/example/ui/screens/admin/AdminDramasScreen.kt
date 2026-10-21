package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Drama
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun AdminDramasScreen(
    dramas: List<Drama>,
    onAddDrama: (Drama) -> Unit,
    onUpdateDrama: (Drama) -> Unit,
    onDeleteDrama: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingDrama by remember { mutableStateOf<Drama?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(16.dp)
            .testTag("admin_dramas_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dramas Manager",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Manage ${dramas.size} mini-series titles & details",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_drama_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Drama", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(dramas) { drama ->
                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = drama.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (drama.isTrending) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = FlameOrange.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "HOT",
                                            color = FlameOrange,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${drama.totalEpisodes} Episodes • ${drama.genres.joinToString(", ")}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = drama.synopsis,
                                fontSize = 10.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { editingDrama = drama },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = AmberGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteDrama(drama.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = NeonCrimson,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add or Edit Drama Dialog
    if (showAddDialog || editingDrama != null) {
        val dramaToEdit = editingDrama
        var title by remember { mutableStateOf(dramaToEdit?.title ?: "") }
        var synopsis by remember { mutableStateOf(dramaToEdit?.synopsis ?: "") }
        var genresStr by remember { mutableStateOf(dramaToEdit?.genres?.joinToString(", ") ?: "Romance, Mafia") }
        var episodesCount by remember { mutableIntStateOf(dramaToEdit?.totalEpisodes ?: 40) }
        var isTrending by remember { mutableStateOf(dramaToEdit?.isTrending ?: false) }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingDrama = null
            },
            title = {
                Text(
                    text = if (dramaToEdit != null) "Edit Drama" else "Add New Mini-Series",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Drama Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = synopsis,
                        onValueChange = { synopsis = it },
                        label = { Text("Synopsis") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = genresStr,
                        onValueChange = { genresStr = it },
                        label = { Text("Genres (comma separated)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = "$episodesCount",
                        onValueChange = { episodesCount = it.toIntOrNull() ?: episodesCount },
                        label = { Text("Total Episodes") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Trending Status:", color = TextSecondary, fontSize = 13.sp)
                        Switch(
                            checked = isTrending,
                            onCheckedChange = { isTrending = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedGenres = genresStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        if (dramaToEdit != null) {
                            onUpdateDrama(
                                dramaToEdit.copy(
                                    title = title,
                                    synopsis = synopsis,
                                    genres = parsedGenres,
                                    totalEpisodes = episodesCount,
                                    isTrending = isTrending
                                )
                            )
                        } else {
                            val newDrama = Drama(
                                id = "drama_${UUID.randomUUID().toString().take(6)}",
                                title = title.ifBlank { "Untitled Series" },
                                synopsis = synopsis.ifBlank { "A thrilling short drama mini-series." },
                                genres = if (parsedGenres.isEmpty()) listOf("Romance") else parsedGenres,
                                totalEpisodes = episodesCount,
                                isTrending = isTrending
                            )
                            onAddDrama(newDrama)
                        }
                        showAddDialog = false
                        editingDrama = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson)
                ) {
                    Text("Save Drama")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        editingDrama = null
                    }
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceElevated
        )
    }
}
