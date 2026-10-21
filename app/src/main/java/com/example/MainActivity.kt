package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.player.ReelsPlayerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.theme.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: DashReelsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashReelsTheme {
                DashReelsApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DashReelsApp(viewModel: DashReelsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val dramas by viewModel.dramas.collectAsStateWithLifecycle()
    val filteredDramas by viewModel.filteredDramas.collectAsStateWithLifecycle()
    val activeDrama by viewModel.activeDrama.collectAsStateWithLifecycle()
    val currentEpisodeIndex by viewModel.currentEpisodeIndex.collectAsStateWithLifecycle()
    val episodesMap by viewModel.episodes.collectAsStateWithLifecycle()
    val unlockedEpisodeIds by viewModel.unlockedEpisodeIds.collectAsStateWithLifecycle()
    val vipPlans by viewModel.vipPlans.collectAsStateWithLifecycle()
    val vipOrders by viewModel.vipOrders.collectAsStateWithLifecycle()
    val paymentConfig by viewModel.paymentConfig.collectAsStateWithLifecycle()
    val monetagConfig by viewModel.monetagConfig.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val showVipDialog by viewModel.showVipDialog.collectAsStateWithLifecycle()
    val adUnlockEpisode by viewModel.adUnlockEpisode.collectAsStateWithLifecycle()
    val notification by viewModel.notification.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val isSyncingFirebase by viewModel.isSyncingFirebase.collectAsStateWithLifecycle()
    val showAdminLoginDialog by viewModel.showAdminLoginDialog.collectAsStateWithLifecycle()
    val adminLoginError by viewModel.adminLoginError.collectAsStateWithLifecycle()

    // Handle system back navigation
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    // Auto dismiss notification after 3 seconds
    LaunchedEffect(notification) {
        if (notification != null) {
            delay(3000)
            viewModel.dismissNotification()
        }
    }

    val isPlayerScreen = currentScreen is Screen.Player

    Box(modifier = Modifier.fillMaxSize().background(OledBlack)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = OledBlack,
            topBar = {
                if (!isPlayerScreen && currentScreen !is Screen.Admin) {
                    DashTopBar(
                        currentScreen = currentScreen,
                        isVip = currentUser.isVip,
                        onSearchClick = { viewModel.navigateTo(Screen.Explore) },
                        onVipClick = { viewModel.openVipSubscriptionDialog() },
                        onAdminClick = { viewModel.requestAdminAccess() }
                    )
                }
            },
            bottomBar = {
                if (!isPlayerScreen) {
                    DashBottomNav(
                        currentScreen = currentScreen,
                        onNavigate = { screen ->
                            if (screen is Screen.Player) {
                                val targetDrama = activeDrama ?: dramas.firstOrNull()
                                if (targetDrama != null) {
                                    viewModel.openDramaPlayer(targetDrama.id, 0)
                                }
                            } else {
                                viewModel.navigateTo(screen)
                            }
                        },
                        onAdminClick = { viewModel.requestAdminAccess() }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isPlayerScreen) PaddingValues(0.dp) else innerPadding)
            ) {
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            dramas = dramas,
                            featuredDrama = activeDrama,
                            monetagConfig = monetagConfig,
                            selectedGenre = selectedGenre,
                            onSelectGenre = { viewModel.setSelectedGenre(it) },
                            onDramaClick = { drama -> viewModel.openDramaPlayer(drama.id, 0) },
                            onPlayClick = { drama -> viewModel.openDramaPlayer(drama.id, 0) }
                        )
                    }

                    is Screen.Explore -> {
                        ExploreScreen(
                            dramas = filteredDramas,
                            searchQuery = searchQuery,
                            selectedGenre = selectedGenre,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onSelectGenre = { viewModel.setSelectedGenre(it) },
                            onDramaClick = { drama -> viewModel.openDramaPlayer(drama.id, 0) }
                        )
                    }

                    is Screen.Player -> {
                        val dramaToPlay = dramas.find { it.id == screen.dramaId } ?: activeDrama ?: dramas.first()
                        val currentEpisodes = episodesMap[dramaToPlay.id] ?: emptyList()

                        ReelsPlayerScreen(
                            drama = dramaToPlay,
                            episodes = currentEpisodes,
                            currentEpisodeIndex = currentEpisodeIndex,
                            unlockedEpisodeIds = unlockedEpisodeIds,
                            isVip = currentUser.isVip,
                            monetagConfig = monetagConfig,
                            onBackClick = { viewModel.navigateBack() },
                            onEpisodeSelect = { idx -> viewModel.selectEpisode(idx) },
                            onNextEpisode = { viewModel.nextEpisode() },
                            onPreviousEpisode = { viewModel.previousEpisode() },
                            onVipClick = { viewModel.openVipSubscriptionDialog() },
                            onAdClick = { viewModel.repository.recordAdClick() }
                        )
                    }

                    is Screen.Admin -> {
                        AdminDashboardScreen(
                            currentTab = screen.subTab,
                            onTabChange = { tab -> viewModel.navigateTo(Screen.Admin(tab)) },
                            onExitAdminMode = { viewModel.navigateTo(Screen.Home) },
                            dramas = dramas,
                            episodesMap = episodesMap,
                            vipOrders = vipOrders,
                            paymentConfig = paymentConfig,
                            monetagConfig = monetagConfig,
                            currentUser = currentUser,
                            onToggleUserVip = { viewModel.repository.toggleCurrentUserVip(it) },
                            onAddDrama = { viewModel.repository.addDrama(it) },
                            onUpdateDrama = { viewModel.repository.updateDrama(it) },
                            onDeleteDrama = { viewModel.repository.deleteDrama(it) },
                            onAddEpisode = { dramaId, ep -> viewModel.repository.addEpisode(dramaId, ep) },
                            onUpdateEpisode = { dramaId, ep -> viewModel.repository.updateEpisode(dramaId, ep) },
                            onDeleteEpisode = { dramaId, epId -> viewModel.repository.deleteEpisode(dramaId, epId) },
                            onUpdatePaymentConfig = { viewModel.repository.updatePaymentConfig(it) },
                            onUpdateOrderStatus = { id, status -> viewModel.repository.updateVipOrderStatus(id, status) },
                            onUpdateMonetagConfig = { viewModel.repository.updateMonetagConfig(it) },
                            isSyncingFirebase = isSyncingFirebase,
                            onSyncFirebaseClick = { viewModel.syncDataToFirebase() },
                            htmlCode = viewModel.getFullHtmlAppCode(),
                            onDownloadHtmlClick = { ctx, cb -> viewModel.downloadHtmlFile(ctx, cb) },
                            onLogoutClick = { viewModel.adminLogout() }
                        )
                    }

                    is Screen.Profile -> {
                        ProfileScreen(
                            user = currentUser,
                            onUpgradeVipClick = { viewModel.openVipSubscriptionDialog() },
                            onOpenAdminClick = { viewModel.requestAdminAccess() }
                        )
                    }
                }
            }
        }

        // Admin Password Login Dialog (Protected by: asif9898)
        if (showAdminLoginDialog) {
            AdminLoginDialog(
                errorMessage = adminLoginError,
                onLoginSubmit = { password -> viewModel.verifyAdminPassword(password) },
                onDismiss = { viewModel.closeAdminLoginDialog() }
            )
        }

        // Ad Unlock Dialog
        if (adUnlockEpisode != null && activeDrama != null) {
            AdUnlockDialog(
                drama = activeDrama!!,
                episode = adUnlockEpisode!!,
                monetagConfig = monetagConfig,
                onUnlockComplete = { viewModel.unlockCurrentAdEpisode() },
                onUpgradeVipClick = { viewModel.openVipSubscriptionDialog() },
                onDismiss = { viewModel.dismissAdUnlockDialog() }
            )
        }

        // VIP Subscription Dialog
        if (showVipDialog) {
            VipSubscriptionDialog(
                vipPlans = vipPlans,
                paymentConfig = paymentConfig,
                onSubmitOrder = { phone, trxId, method, plan ->
                    viewModel.submitVipOrder(phone, trxId, method, plan)
                },
                onDismiss = { viewModel.closeVipSubscriptionDialog() }
            )
        }

        // Temporary Floating Toast Notification
        AnimatedVisibility(
            visible = notification != null,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            notification?.let { notif ->
                Surface(
                    color = if (notif.isError) NeonCrimson else SurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    shadowElevation = 8.dp,
                    modifier = Modifier.testTag("floating_notification")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (notif.isError) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (notif.isError) TextPrimary else EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = notif.message,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}
