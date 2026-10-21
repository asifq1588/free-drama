package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.*

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen,
    val testTag: String
)

@Composable
fun DashBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onAdminClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem("Home", Icons.Default.Home, Screen.Home, "nav_home"),
        NavItem("Explore", Icons.Default.Explore, Screen.Explore, "nav_explore"),
        NavItem("Watch", Icons.Default.PlayCircle, Screen.Player("drama_1", 0), "nav_player"),
        NavItem("Admin", Icons.Default.AdminPanelSettings, Screen.Admin(), "nav_admin"),
        NavItem("Profile", Icons.Default.Person, Screen.Profile, "nav_profile")
    )

    Surface(
        color = SurfaceDark,
        tonalElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = when (item.screen) {
                    is Screen.Home -> currentScreen is Screen.Home
                    is Screen.Explore -> currentScreen is Screen.Explore
                    is Screen.Player -> currentScreen is Screen.Player
                    is Screen.Admin -> currentScreen is Screen.Admin
                    is Screen.Profile -> currentScreen is Screen.Profile
                }

                val activeColor = if (item.screen is Screen.Admin) AmberGold else NeonCrimson

                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            if (item.screen is Screen.Admin) {
                                onAdminClick()
                            } else {
                                onNavigate(item.screen)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag(item.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) activeColor else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.title,
                        color = if (isSelected) activeColor else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
