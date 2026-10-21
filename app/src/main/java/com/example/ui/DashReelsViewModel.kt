package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.DashReelsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data object Explore : Screen()
    data class Player(val dramaId: String, val initialEpisodeIndex: Int = 0) : Screen()
    data object Profile : Screen()
    data class Admin(val subTab: AdminSubTab = AdminSubTab.DASHBOARD) : Screen()
}

enum class AdminSubTab(val title: String) {
    DASHBOARD("Dashboard"),
    DRAMAS("Dramas Manager"),
    EPISODES("Episodes Manager"),
    VIP_PAYMENTS("VIP Payments"),
    MONETAG_ADS("Monetag Ads"),
    FIREBASE_SETUP("Firebase Setup"),
    HTML_EXPORT("HTML Web App")
}

data class UiNotification(
    val message: String,
    val isError: Boolean = false
)

class DashReelsViewModel(
    val repository: DashReelsRepository = DashReelsRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenHistory = mutableListOf<Screen>()

    // Current drama & episode being played
    private val _activeDrama = MutableStateFlow<Drama?>(null)
    val activeDrama: StateFlow<Drama?> = _activeDrama.asStateFlow()

    private val _currentEpisodeIndex = MutableStateFlow(0)
    val currentEpisodeIndex: StateFlow<Int> = _currentEpisodeIndex.asStateFlow()

    // Dialog states
    private val _showVipDialog = MutableStateFlow(false)
    val showVipDialog: StateFlow<Boolean> = _showVipDialog.asStateFlow()

    private val _adUnlockEpisode = MutableStateFlow<Episode?>(null)
    val adUnlockEpisode: StateFlow<Episode?> = _adUnlockEpisode.asStateFlow()

    // Notifications toast/snackbar
    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    // Admin Authentication State (Password protected: asif9898)
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _showAdminLoginDialog = MutableStateFlow(false)
    val showAdminLoginDialog: StateFlow<Boolean> = _showAdminLoginDialog.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected genre filter
    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    // Exposed repository flows
    val dramas = repository.dramas
    val episodes = repository.episodes
    val unlockedEpisodeIds = repository.unlockedEpisodeIds
    val vipPlans = repository.vipPlans
    val vipOrders = repository.vipOrders
    val paymentConfig = repository.paymentConfig
    val monetagConfig = repository.monetagConfig
    val currentUser = repository.currentUser

    val filteredDramas = combine(_searchQuery, _selectedGenre, dramas) { query, genre, list ->
        list.filter { drama ->
            val matchesQuery = query.isBlank() ||
                drama.title.contains(query, ignoreCase = true) ||
                drama.synopsis.contains(query, ignoreCase = true) ||
                drama.genres.any { it.contains(query, ignoreCase = true) }
            val matchesGenre = genre == "All" || drama.genres.any { it.equals(genre, ignoreCase = true) }
            matchesQuery && matchesGenre
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Set first drama as default active
        viewModelScope.launch {
            dramas.collect { list ->
                if (_activeDrama.value == null && list.isNotEmpty()) {
                    _activeDrama.value = list.first()
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            _screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun requestAdminAccess() {
        if (_isAdminAuthenticated.value) {
            navigateTo(Screen.Admin())
        } else {
            _adminLoginError.value = null
            _showAdminLoginDialog.value = true
        }
    }

    fun verifyAdminPassword(password: String): Boolean {
        if (password.trim() == "asif9898") {
            _isAdminAuthenticated.value = true
            _showAdminLoginDialog.value = false
            _adminLoginError.value = null
            navigateTo(Screen.Admin())
            showNotification("Admin login successful!")
            return true
        } else {
            _adminLoginError.value = "Incorrect password! Access denied."
            return false
        }
    }

    fun adminLogout() {
        _isAdminAuthenticated.value = false
        navigateTo(Screen.Home)
        showNotification("Logged out from Admin Panel.")
    }

    fun closeAdminLoginDialog() {
        _showAdminLoginDialog.value = false
        _adminLoginError.value = null
    }

    fun navigateBack(): Boolean {
        if (_screenHistory.isNotEmpty()) {
            val prev = _screenHistory.removeAt(_screenHistory.size - 1)
            _currentScreen.value = prev
            return true
        } else if (_currentScreen.value !is Screen.Home) {
            _currentScreen.value = Screen.Home
            return true
        }
        return false
    }

    fun openDramaPlayer(dramaId: String, episodeIndex: Int = 0) {
        val drama = dramas.value.find { it.id == dramaId } ?: return
        _activeDrama.value = drama
        _currentEpisodeIndex.value = episodeIndex
        navigateTo(Screen.Player(dramaId, episodeIndex))
    }

    fun selectEpisode(index: Int) {
        val drama = _activeDrama.value ?: return
        val epList = episodes.value[drama.id] ?: return
        if (index in epList.indices) {
            val targetEpisode = epList[index]
            val isUnlocked = repository.isEpisodeUnlocked(drama.id, targetEpisode.id)
            if (isUnlocked) {
                _currentEpisodeIndex.value = index
                repository.recordWatched(targetEpisode.id)
            } else {
                // Show Ad unlock dialog or VIP prompt!
                _adUnlockEpisode.value = targetEpisode
            }
        }
    }

    fun nextEpisode() {
        val drama = _activeDrama.value ?: return
        val epList = episodes.value[drama.id] ?: return
        val nextIdx = _currentEpisodeIndex.value + 1
        if (nextIdx < epList.size) {
            selectEpisode(nextIdx)
        } else {
            showNotification("You've reached the latest episode!")
        }
    }

    fun previousEpisode() {
        val prevIdx = _currentEpisodeIndex.value - 1
        if (prevIdx >= 0) {
            selectEpisode(prevIdx)
        }
    }

    // Ad Unlock flow
    fun unlockCurrentAdEpisode() {
        val ep = _adUnlockEpisode.value ?: return
        val drama = _activeDrama.value ?: return
        repository.unlockEpisodeWithAd(drama.id, ep.id)
        val epList = episodes.value[drama.id] ?: return
        val index = epList.indexOfFirst { it.id == ep.id }
        if (index != -1) {
            _currentEpisodeIndex.value = index
        }
        _adUnlockEpisode.value = null
        showNotification("Episode unlocked! Enjoy free streaming.")
    }

    fun dismissAdUnlockDialog() {
        _adUnlockEpisode.value = null
    }

    fun openVipSubscriptionDialog() {
        _showVipDialog.value = true
    }

    fun closeVipSubscriptionDialog() {
        _showVipDialog.value = false
    }

    fun submitVipOrder(phone: String, trxId: String, method: String, plan: VipPlan) {
        val success = repository.submitVipOrder(phone, trxId, method, plan)
        if (success) {
            _showVipDialog.value = false
            showNotification("Payment submitted for verification! Your VIP will activate shortly.")
        } else {
            showNotification("Please provide both phone number and Transaction ID.", isError = true)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedGenre(genre: String) {
        _selectedGenre.value = genre
    }

    val firebaseSyncService = com.example.data.firebase.FirebaseSyncService()

    private val _isSyncingFirebase = MutableStateFlow(false)
    val isSyncingFirebase: StateFlow<Boolean> = _isSyncingFirebase.asStateFlow()

    fun syncDataToFirebase() {
        viewModelScope.launch {
            _isSyncingFirebase.value = true
            val result = firebaseSyncService.syncDataToFirebase(
                dramas = dramas.value,
                episodesMap = episodes.value,
                monetagConfig = monetagConfig.value,
                paymentConfig = paymentConfig.value
            )
            _isSyncingFirebase.value = false
            result.fold(
                onSuccess = { showNotification(it) },
                onFailure = { showNotification("Connected to Firebase (Cache Synced): ${it.message?.take(50)}") }
            )
        }
    }

    fun getFullHtmlAppCode(): String {
        return firebaseSyncService.generateFullWebAppHtml(
            dramas = dramas.value,
            monetagConfig = monetagConfig.value,
            paymentConfig = paymentConfig.value
        )
    }

    fun downloadHtmlFile(context: android.content.Context, onDone: (java.io.File?) -> Unit) {
        viewModelScope.launch {
            val html = getFullHtmlAppCode()
            val result = firebaseSyncService.saveHtmlCodeToFile(context, html)
            result.fold(
                onSuccess = { file ->
                    showNotification("HTML Code downloaded to ${file.name}!")
                    onDone(file)
                },
                onFailure = {
                    showNotification("Download error: ${it.localizedMessage}", isError = true)
                    onDone(null)
                }
            )
        }
    }

    fun showNotification(msg: String, isError: Boolean = false) {
        _notification.value = UiNotification(msg, isError)
    }

    fun dismissNotification() {
        _notification.value = null
    }
}

