package com.example.data.repository

import com.example.R
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class DashReelsRepository {

    private val sampleVideos = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    )

    private val _dramas = MutableStateFlow<List<Drama>>(emptyList())
    val dramas: StateFlow<List<Drama>> = _dramas.asStateFlow()

    private val _episodes = MutableStateFlow<Map<String, List<Episode>>>(emptyMap())
    val episodes: StateFlow<Map<String, List<Episode>>> = _episodes.asStateFlow()

    private val _unlockedEpisodeIds = MutableStateFlow<Set<String>>(emptySet())
    val unlockedEpisodeIds: StateFlow<Set<String>> = _unlockedEpisodeIds.asStateFlow()

    private val _vipPlans = MutableStateFlow<List<VipPlan>>(emptyList())
    val vipPlans: StateFlow<List<VipPlan>> = _vipPlans.asStateFlow()

    private val _vipOrders = MutableStateFlow<List<VipOrder>>(emptyList())
    val vipOrders: StateFlow<List<VipOrder>> = _vipOrders.asStateFlow()

    private val _paymentConfig = MutableStateFlow(PaymentGatewayConfig())
    val paymentConfig: StateFlow<PaymentGatewayConfig> = _paymentConfig.asStateFlow()

    private val _monetagConfig = MutableStateFlow(MonetagAdConfig())
    val monetagConfig: StateFlow<MonetagAdConfig> = _monetagConfig.asStateFlow()

    private val _currentUser = MutableStateFlow(UserAccount())
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    init {
        initializeSampleData()
    }

    private fun initializeSampleData() {
        val dramaList = listOf(
            Drama(
                id = "drama_1",
                title = "The Twins Love Me Too Late",
                synopsis = "For three years, Hannah slept beside a stranger — her husband's twin, Ansel — in a marriage built on Aiden's revenge scheme for Ivy. After her mother's death and a secret pregnancy, Hannah demands a divorce.",
                coverDrawableRes = R.drawable.poster_twins_love,
                genres = listOf("Regret", "Mafia", "Marriage", "Twisted", "Revenge", "Betrayal"),
                totalEpisodes = 54,
                rating = 4.9f,
                views = "24.5M",
                isTrending = true,
                isExclusive = true,
                language = "English"
            ),
            Drama(
                id = "drama_2",
                title = "The Alpha King's Mate",
                synopsis = "Banished from her birth pack as a powerless rogue, Luna discovers on the blood moon that the ruthless Lycan King of the Northern Realms is her fated mate. A fiery saga of royalty, forbidden desire and raw power.",
                coverDrawableRes = R.drawable.poster_alpha_king,
                genres = listOf("Werewolf", "Fantasy", "Alpha", "Royal Romance", "Enemies to Lovers"),
                totalEpisodes = 48,
                rating = 4.8f,
                views = "19.2M",
                isTrending = true,
                isExclusive = true,
                language = "English"
            ),
            Drama(
                id = "drama_3",
                title = "The Contract Bride",
                synopsis = "To save her family's empire from bankruptcy, Sophia agrees to marry Liam Vance, the icy billionaire heir to the Vance syndicate. The rule was simple: No falling in love. But every kiss breaks the terms.",
                coverDrawableRes = R.drawable.poster_contract_bride,
                genres = listOf("Billionaire", "Contract Marriage", "Mafia", "Second Chance"),
                totalEpisodes = 52,
                rating = 4.9f,
                views = "15.7M",
                isTrending = true,
                isExclusive = false,
                language = "English"
            ),
            Drama(
                id = "drama_4",
                title = "Reset Queen (Hindi Dubbed)",
                synopsis = "Betrayed by her stepsister and murdered on her coronation night, Princess Aanya awakens ten years in the past. Armed with memories of the future, the reborn queen plots supreme revenge.",
                coverDrawableRes = R.drawable.poster_twins_love,
                genres = listOf("Rebirth", "Revenge", "Hindi Dubbed", "Adventure", "Royalty"),
                totalEpisodes = 51,
                rating = 4.7f,
                views = "8.3M",
                isTrending = false,
                isExclusive = false,
                language = "Hindi Dubbed"
            ),
            Drama(
                id = "drama_5",
                title = "Top Gear Guy Finds His Mrs. Right",
                synopsis = "A street racer with a golden heart saves an undercover heiress on the neon streets of Tokyo. Speed, adrenaline, and unexpected romance collide.",
                coverDrawableRes = R.drawable.poster_contract_bride,
                genres = listOf("Romance", "Action", "Billionaire", "Comedy"),
                totalEpisodes = 45,
                rating = 4.6f,
                views = "6.1M",
                isTrending = false,
                isExclusive = false,
                language = "English"
            ),
            Drama(
                id = "drama_6",
                title = "Don's Back: No One Touches His Daughter",
                synopsis = "The legendary underworld boss retired a decade ago to raise his daughter in peace. When a ruthless cartel crosses the line, the Don returns with a vengeance.",
                coverDrawableRes = R.drawable.poster_alpha_king,
                genres = listOf("Mafia", "Action", "Crime", "Protection"),
                totalEpisodes = 60,
                rating = 4.9f,
                views = "31.0M",
                isTrending = true,
                isExclusive = true,
                language = "English"
            )
        )
        _dramas.value = dramaList

        // Build 12 initial episodes for each drama with mix of Free, Ad-unlock, and VIP
        val episodeMap = mutableMapOf<String, List<Episode>>()
        val unlocked = mutableSetOf<String>()

        dramaList.forEach { drama ->
            val list = mutableListOf<Episode>()
            val count = minOf(drama.totalEpisodes, 15)
            for (i in 1..count) {
                val epId = "${drama.id}_ep_$i"
                val accessType = when {
                    i <= 3 -> EpisodeAccessType.FREE
                    i <= 8 -> EpisodeAccessType.AD_REQUIRED
                    else -> EpisodeAccessType.VIP_ONLY
                }
                if (accessType == EpisodeAccessType.FREE) {
                    unlocked.add(epId)
                }

                // Sample Monetag direct link or zone custom per episode
                val episodeMonetagAd = if (accessType == EpisodeAccessType.AD_REQUIRED) {
                    "https://otieuwew.net/4/8592147?sub1=${drama.id}&sub2=ep_$i"
                } else ""

                list.add(
                    Episode(
                        id = epId,
                        dramaId = drama.id,
                        episodeNumber = i,
                        title = "Episode $i",
                        videoUrl = sampleVideos[(i - 1) % sampleVideos.size],
                        thumbnailUrl = "",
                        durationSeconds = 90 + (i * 7) % 60,
                        accessType = accessType,
                        monetagAdUrl = episodeMonetagAd,
                        isUnlocked = accessType == EpisodeAccessType.FREE,
                        viewsCount = 350000 - (i * 12000)
                    )
                )
            }
            episodeMap[drama.id] = list
        }
        _episodes.value = episodeMap
        _unlockedEpisodeIds.value = unlocked

        // VIP Subscription Plans
        _vipPlans.value = listOf(
            VipPlan(
                id = "plan_weekly",
                name = "Weekly VIP",
                price = "49৳",
                durationLabel = "7 Days",
                durationDays = 7,
                isPopular = false,
                isBestValue = false,
                features = listOf("Unlock All VIP Episodes", "Ad-Free High Definition Streaming", "Watch on 1 Device")
            ),
            VipPlan(
                id = "plan_monthly",
                name = "Monthly VIP",
                price = "149৳",
                durationLabel = "30 Days",
                durationDays = 30,
                isPopular = true,
                isBestValue = false,
                features = listOf("Unlock All Series & Episodes", "Full 4K Ultra HD & Dolby Audio", "Skip All Monetag Ads", "Early Access to New Releases")
            ),
            VipPlan(
                id = "plan_yearly",
                name = "Yearly VIP",
                price = "999৳",
                durationLabel = "365 Days",
                durationDays = 365,
                isPopular = false,
                isBestValue = true,
                features = listOf("365 Days Full Unlimited VIP", "100% Ad-Free Experience", "Download for Offline Viewing", "VIP Priority Badge & Support")
            )
        )

        // Initial sample VIP orders for Admin to manage
        _vipOrders.value = listOf(
            VipOrder(
                id = "ord_001",
                senderPhone = "01812345678",
                trxId = "BL98K23N09",
                paymentMethod = "bKash",
                planId = "plan_monthly",
                planName = "Monthly VIP",
                amount = "149৳",
                timestamp = System.currentTimeMillis() - 3600000L * 2,
                status = VipOrderStatus.PENDING
            ),
            VipOrder(
                id = "ord_002",
                senderPhone = "01755566778",
                trxId = "NG88X19M42",
                paymentMethod = "Nagad",
                planId = "plan_weekly",
                planName = "Weekly VIP",
                amount = "49৳",
                timestamp = System.currentTimeMillis() - 3600000L * 24,
                status = VipOrderStatus.APPROVED
            )
        )
    }

    // Episode unlock check
    fun isEpisodeUnlocked(dramaId: String, episodeId: String): Boolean {
        if (_currentUser.value.isVip) return true
        if (_unlockedEpisodeIds.value.contains(episodeId)) return true
        val ep = _episodes.value[dramaId]?.find { it.id == episodeId }
        return ep?.accessType == EpisodeAccessType.FREE
    }

    // Unlock episode via Monetag Ad reward
    fun unlockEpisodeWithAd(dramaId: String, episodeId: String) {
        _unlockedEpisodeIds.update { it + episodeId }
        _monetagConfig.update { it.copy(impressionsCount = it.impressionsCount + 1) }
    }

    // Record Monetag Ad Click
    fun recordAdClick() {
        _monetagConfig.update { it.copy(clicksCount = it.clicksCount + 1) }
    }

    // Submit new VIP payment order
    fun submitVipOrder(phone: String, trxId: String, method: String, plan: VipPlan): Boolean {
        if (phone.isBlank() || trxId.isBlank()) return false
        val newOrder = VipOrder(
            id = "ord_${UUID.randomUUID().toString().take(6)}",
            senderPhone = phone.trim(),
            trxId = trxId.trim().uppercase(),
            paymentMethod = method,
            planId = plan.id,
            planName = plan.name,
            amount = plan.price,
            timestamp = System.currentTimeMillis(),
            status = VipOrderStatus.PENDING
        )
        _vipOrders.update { listOf(newOrder) + it }
        return true
    }

    // Admin updates VIP order status
    fun updateVipOrderStatus(orderId: String, newStatus: VipOrderStatus) {
        _vipOrders.update { list ->
            list.map { order ->
                if (order.id == orderId) {
                    order.copy(status = newStatus)
                } else order
            }
        }
        if (newStatus == VipOrderStatus.APPROVED) {
            // Activate VIP for current demo user
            _currentUser.update {
                it.copy(
                    isVip = true,
                    vipPlanName = "Monthly VIP Active",
                    vipExpireDate = "30 Days Remaining"
                )
            }
        }
    }

    // Toggle VIP status directly (for Admin testing)
    fun toggleCurrentUserVip(isVip: Boolean) {
        _currentUser.update {
            it.copy(
                isVip = isVip,
                vipPlanName = if (isVip) "Admin Activated VIP" else "",
                vipExpireDate = if (isVip) "Unlimited Access" else ""
            )
        }
    }

    // Admin Drama management
    fun addDrama(drama: Drama) {
        _dramas.update { it + drama }
        // create empty episode list for this drama
        _episodes.update {
            it + (drama.id to listOf(
                Episode(
                    id = "${drama.id}_ep_1",
                    dramaId = drama.id,
                    episodeNumber = 1,
                    title = "Episode 1",
                    videoUrl = sampleVideos[0],
                    accessType = EpisodeAccessType.FREE,
                    isUnlocked = true
                )
            ))
        }
    }

    fun updateDrama(drama: Drama) {
        _dramas.update { list ->
            list.map { if (it.id == drama.id) drama else it }
        }
    }

    fun deleteDrama(dramaId: String) {
        _dramas.update { list -> list.filter { it.id != dramaId } }
        _episodes.update { map -> map.filterKeys { it != dramaId } }
    }

    // Admin Episode management per episode!
    fun addEpisode(dramaId: String, episode: Episode) {
        _episodes.update { map ->
            val current = map[dramaId]?.toMutableList() ?: mutableListOf()
            current.add(episode)
            map + (dramaId to current.sortedBy { it.episodeNumber })
        }
    }

    fun updateEpisode(dramaId: String, episode: Episode) {
        _episodes.update { map ->
            val current = map[dramaId]?.map {
                if (it.id == episode.id) episode else it
            } ?: emptyList()
            map + (dramaId to current)
        }
    }

    fun deleteEpisode(dramaId: String, episodeId: String) {
        _episodes.update { map ->
            val current = map[dramaId]?.filter { it.id != episodeId } ?: emptyList()
            map + (dramaId to current)
        }
    }

    // Admin update Monetag Ad settings
    fun updateMonetagConfig(config: MonetagAdConfig) {
        _monetagConfig.value = config
    }

    // Admin update Payment Gateway numbers
    fun updatePaymentConfig(config: PaymentGatewayConfig) {
        _paymentConfig.value = config
    }

    // Toggle favorite bookmark
    fun toggleFavorite(dramaId: String) {
        _currentUser.update { user ->
            val currentFavorites = user.favoriteDramaIds
            val updated = if (currentFavorites.contains(dramaId)) {
                currentFavorites - dramaId
            } else {
                currentFavorites + dramaId
            }
            user.copy(favoriteDramaIds = updated)
        }
    }

    fun recordWatched(episodeId: String) {
        _currentUser.update {
            it.copy(watchedEpisodeIds = it.watchedEpisodeIds + episodeId)
        }
    }
}
