package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Drama
import com.example.data.model.MonetagAdConfig
import com.example.ui.components.DramaPosterCard
import com.example.ui.components.MonetagBannerCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    dramas: List<Drama>,
    featuredDrama: Drama?,
    monetagConfig: MonetagAdConfig,
    selectedGenre: String,
    onSelectGenre: (String) -> Unit,
    onDramaClick: (Drama) -> Unit,
    onPlayClick: (Drama) -> Unit,
    modifier: Modifier = Modifier
) {
    val genresList = listOf("All", "Romance", "Mafia", "Werewolf", "Billionaire", "Hindi Dubbed", "Revenge")
    val heroDrama = featuredDrama ?: dramas.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Featured Carousel
        if (heroDrama != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                        .clickable { onDramaClick(heroDrama) }
                        .testTag("hero_featured_drama")
                ) {
                    // Hero Image
                    if (heroDrama.coverDrawableRes != null) {
                        Image(
                            painter = painterResource(id = heroDrama.coverDrawableRes),
                            contentDescription = heroDrama.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Gradients
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        OledBlack.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        OledBlack.copy(alpha = 0.85f),
                                        OledBlack
                                    )
                                )
                            )
                    )

                    // Hero Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        // Tags row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(heroDrama.genres) { tag ->
                                Surface(
                                    color = SurfaceDark.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Title
                        Text(
                            text = heroDrama.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = TextPrimary,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Stats: Rating & Episodes
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${heroDrama.rating}",
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Text(text = "•", color = TextMuted, fontSize = 12.sp)
                            Text(
                                text = "${heroDrama.totalEpisodes} Episodes",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(text = "•", color = TextMuted, fontSize = 12.sp)
                            Text(
                                text = heroDrama.views + " Views",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Synopsis preview
                        Text(
                            text = heroDrama.synopsis,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Play Button
                        Button(
                            onClick = { onPlayClick(heroDrama) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("hero_play_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Episode 1",
                                tint = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Play Episode 1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Genre Filter Chips Row
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genresList) { genre ->
                    val isSelected = selectedGenre == genre
                    Surface(
                        color = if (isSelected) NeonCrimson else SurfaceDark,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectGenre(genre) }
                            .testTag("genre_chip_$genre")
                    ) {
                        Text(
                            text = genre,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Monetag Sponsored Ad Card
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                MonetagBannerCard(monetagConfig = monetagConfig)
            }
        }

        // Section: Trending Now
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trending Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            color = NeonCrimson,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View All",
                            tint = NeonCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dramas) { drama ->
                        DramaPosterCard(
                            drama = drama,
                            onClick = { onDramaClick(drama) },
                            modifier = Modifier
                                .width(135.dp)
                                .height(210.dp)
                        )
                    }
                }
            }
        }

        // Section: Billionaire & Mafia Romance
        item {
            val mafiaDramas = dramas.filter { d ->
                d.genres.any { it.contains("Mafia", ignoreCase = true) || it.contains("Billionaire", ignoreCase = true) }
            }
            if (mafiaDramas.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 22.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Billionaire & Mafia Sagas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(mafiaDramas) { drama ->
                            DramaPosterCard(
                                drama = drama,
                                onClick = { onDramaClick(drama) },
                                modifier = Modifier
                                    .width(135.dp)
                                    .height(210.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: Hindi Dubbed & Global Hits
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 22.dp)) {
                Text(
                    text = "Dubbed Hits & Rebirth Romances",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dramas.reversed()) { drama ->
                        DramaPosterCard(
                            drama = drama,
                            onClick = { onDramaClick(drama) },
                            modifier = Modifier
                                .width(135.dp)
                                .height(210.dp)
                        )
                    }
                }
            }
        }
    }
}
