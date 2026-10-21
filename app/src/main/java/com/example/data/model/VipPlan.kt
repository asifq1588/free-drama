package com.example.data.model

enum class VipOrderStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class VipPlan(
    val id: String,
    val name: String,
    val price: String,
    val durationLabel: String,
    val durationDays: Int,
    val isPopular: Boolean = false,
    val isBestValue: Boolean = false,
    val features: List<String>
)

data class VipOrder(
    val id: String,
    val senderPhone: String,
    val trxId: String,
    val paymentMethod: String, // "bKash" or "Nagad"
    val planId: String,
    val planName: String,
    val amount: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: VipOrderStatus = VipOrderStatus.PENDING
)

data class MonetagAdConfig(
    val isEnabled: Boolean = true,
    val globalDirectLinkUrl: String = "https://otieuwew.net/4/8592147", // Monetag direct link demo
    val bannerZoneId: String = "8592148",
    val rewardedZoneId: String = "8592149",
    val interstitialFrequency: Int = 3,
    val adDurationSeconds: Int = 5,
    val impressionsCount: Int = 14280,
    val clicksCount: Int = 3290
)

data class PaymentGatewayConfig(
    val isPaymentActive: Boolean = true,
    val bKashNumber: String = "01706457598",
    val bKashAccountType: String = "Personal (Send Money)",
    val nagadNumber: String = "01999999999",
    val nagadAccountType: String = "Personal (Send Money)",
    val instructions: String = "Send Money to the bKash or Nagad number. After sending, enter your sender mobile number and Transaction ID (TrxID) below to activate VIP instantly."
)

data class UserAccount(
    val uid: String = "user_demo_789",
    val displayName: String = "Cinema Fan",
    val email: String = "user@dashreels.com",
    val isVip: Boolean = false,
    val vipPlanName: String = "",
    val vipExpireDate: String = "",
    val coins: Int = 150,
    val watchedEpisodeIds: Set<String> = emptySet(),
    val favoriteDramaIds: Set<String> = setOf("drama_1", "drama_2")
)
