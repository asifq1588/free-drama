package com.example.data.firebase

import android.content.Context
import android.os.Environment
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.data.model.MonetagAdConfig
import com.example.data.model.PaymentGatewayConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object FirebaseConfigData {
    const val API_KEY = "AIzaSyCy2xiyARWURfEiSJa30Hg3m6UbXZBF6Ec"
    const val AUTH_DOMAIN = "dashreels-bfb61.firebaseapp.com"
    const val DATABASE_URL = "https://dashreels-bfb61-default-rtdb.firebaseio.com"
    const val PROJECT_ID = "dashreels-bfb61"
    const val STORAGE_BUCKET = "dashreels-bfb61.firebasestorage.app"
    const val MESSAGING_SENDER_ID = "146956493137"
    const val APP_ID = "1:146956493137:web:de04087d096f2cc24cb370"
}

class FirebaseSyncService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // Sync all Dramas and Episodes to Firebase Realtime Database
    suspend fun syncDataToFirebase(
        dramas: List<Drama>,
        episodesMap: Map<String, List<Episode>>,
        monetagConfig: MonetagAdConfig,
        paymentConfig: PaymentGatewayConfig
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val rootJson = JSONObject().apply {
                val dramasJson = JSONObject()
                dramas.forEach { drama ->
                    val dramaObj = JSONObject().apply {
                        put("id", drama.id)
                        put("title", drama.title)
                        put("synopsis", drama.synopsis)
                        put("genres", JSONArray(drama.genres))
                        put("totalEpisodes", drama.totalEpisodes)
                        put("rating", drama.rating.toDouble())
                        put("views", drama.views)
                        put("isTrending", drama.isTrending)
                        put("isExclusive", drama.isExclusive)
                        put("language", drama.language)
                        put("coverUrl", drama.coverUrl)

                        val eps = episodesMap[drama.id] ?: emptyList()
                        val epsObj = JSONObject()
                        eps.forEach { ep ->
                            val epObj = JSONObject().apply {
                                put("id", ep.id)
                                put("episodeNumber", ep.episodeNumber)
                                put("title", ep.title)
                                put("videoUrl", ep.videoUrl)
                                put("accessType", ep.accessType.name)
                                put("monetagAdUrl", ep.monetagAdUrl)
                                put("durationSeconds", ep.durationSeconds)
                            }
                            epsObj.put(ep.id, epObj)
                        }
                        put("episodes", epsObj)
                    }
                    dramasJson.put(drama.id, dramaObj)
                }
                put("dramas", dramasJson)

                val adsJson = JSONObject().apply {
                    put("isEnabled", monetagConfig.isEnabled)
                    put("globalDirectLinkUrl", monetagConfig.globalDirectLinkUrl)
                    put("bannerZoneId", monetagConfig.bannerZoneId)
                    put("rewardedZoneId", monetagConfig.rewardedZoneId)
                    put("interstitialFrequency", monetagConfig.interstitialFrequency)
                }
                put("monetagConfig", adsJson)

                val paymentsJson = JSONObject().apply {
                    put("isPaymentActive", paymentConfig.isPaymentActive)
                    put("bKashNumber", paymentConfig.bKashNumber)
                    put("nagadNumber", paymentConfig.nagadNumber)
                    put("instructions", paymentConfig.instructions)
                }
                put("paymentConfig", paymentsJson)
                put("lastUpdated", System.currentTimeMillis())
            }

            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "${FirebaseConfigData.DATABASE_URL}/dashreels_app.json"
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success("Synced successfully to Firebase Realtime Database (${FirebaseConfigData.PROJECT_ID})!")
            } else {
                Result.failure(Exception("Firebase returned status code: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Save full HTML code to Download folder
    suspend fun saveHtmlCodeToFile(context: Context, htmlContent: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir
            val file = File(downloadsDir, "dashreels_web_app.html")
            FileOutputStream(file).use { out ->
                out.write(htmlContent.toByteArray(Charsets.UTF_8))
                out.flush()
            }
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Generate Complete Standalone HTML/CSS/JS Application with Firebase + Monetag + bKash/Nagad
    fun generateFullWebAppHtml(
        dramas: List<Drama>,
        monetagConfig: MonetagAdConfig,
        paymentConfig: PaymentGatewayConfig
    ): String {
        return """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DashReels - Short Drama Mini-Series</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
  <style>
    :root {
      --bg: #08080C;
      --surface: #12121A;
      --elevated: #1B1B26;
      --card: #20202E;
      --crimson: #FF2D55;
      --orange: #FF5722;
      --gold: #FFB300;
      --green: #00E676;
      --text: #FFFFFF;
      --text-muted: #8E8EA8;
      --border: #2C2C3E;
    }
    * { margin:0; padding:0; box-sizing:border-box; font-family:'Plus Jakarta Sans',sans-serif; }
    body { background: var(--bg); color: var(--text); overflow-x: hidden; }
    header {
      display: flex; justify-content: space-between; align-items: center;
      padding: 14px 20px; background: rgba(8,8,12,0.95);
      border-bottom: 1px solid var(--border); position: sticky; top:0; z-index: 100;
      backdrop-filter: blur(10px);
    }
    .logo { display: flex; align-items: center; gap: 8px; font-weight: 800; font-size: 20px; }
    .logo-badge {
      width: 32px; height: 32px; border-radius: 8px;
      background: linear-gradient(135deg, var(--crimson), var(--orange));
      display: flex; align-items: center; justify-content: center; font-size: 16px;
    }
    .btn-vip {
      background: rgba(255,179,0,0.15); border: 1px solid var(--gold);
      color: var(--gold); font-weight: 700; font-size: 12px;
      padding: 7px 14px; border-radius: 20px; cursor: pointer; transition: 0.2s;
    }
    .btn-vip:hover { background: var(--gold); color: #000; }
    
    .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    
    /* Hero player section */
    .stream-layout {
      display: grid; grid-template-columns: 1fr 340px; gap: 24px;
      margin-bottom: 40px;
    }
    @media(max-width: 860px) {
      .stream-layout { grid-template-columns: 1fr; }
    }
    
    .player-card {
      background: #000; border-radius: 16px; overflow: hidden;
      border: 1px solid var(--border); position: relative;
      aspect-ratio: 9/16; max-height: 640px; margin: 0 auto; width: 100%; max-width: 360px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.7);
    }
    video { width: 100%; height: 100%; object-fit: cover; }
    
    .episodes-panel {
      background: var(--surface); border-radius: 16px; padding: 18px;
      border: 1px solid var(--border); max-height: 640px; display: flex; flex-direction: column;
    }
    .episodes-grid {
      display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px;
      overflow-y: auto; padding-right: 4px; margin-top: 12px;
    }
    .ep-btn {
      background: var(--elevated); border: 1px solid var(--border);
      color: var(--text); border-radius: 8px; padding: 12px 6px;
      text-align: center; font-weight: 700; font-size: 13px; cursor: pointer;
      position: relative; transition: 0.2s;
    }
    .ep-btn.active { background: rgba(255,45,85,0.25); border-color: var(--crimson); color: var(--crimson); }
    .ep-badge {
      position: absolute; top: 3px; right: 3px; font-size: 8px;
      padding: 1px 4px; border-radius: 4px; font-weight: 800;
    }
    .ep-badge.ad { background: var(--orange); color: #fff; }
    .ep-badge.vip { background: var(--gold); color: #000; }
    
    /* Monetag Ad Banner */
    .monetag-banner {
      background: linear-gradient(90deg, #1b1626, #201a1c);
      border: 1px solid var(--orange); border-radius: 12px;
      padding: 12px 18px; display: flex; justify-content: space-between; align-items: center;
      margin: 20px 0; cursor: pointer; text-decoration: none; color: inherit;
    }
    
    /* Modals */
    .modal-overlay {
      position: fixed; inset: 0; background: rgba(0,0,0,0.85);
      display: none; align-items: center; justify-content: center; z-index: 200; padding: 20px;
    }
    .modal-box {
      background: var(--elevated); border: 1px solid var(--border);
      border-radius: 20px; max-width: 480px; width: 100%; padding: 24px;
      position: relative; animation: popIn 0.2s ease;
    }
    @keyframes popIn { from { transform: scale(0.95); opacity: 0; } to { transform: scale(1); opacity: 1; } }
    .close-btn { position: absolute; top: 18px; right: 18px; background: none; border: none; color: var(--text-muted); font-size: 20px; cursor: pointer; }
    
    .plan-card {
      background: var(--surface); border: 1.5px solid var(--border);
      border-radius: 12px; padding: 12px; cursor: pointer; text-align: center;
      transition: 0.2s;
    }
    .plan-card.selected { border-color: var(--gold); background: rgba(255,179,0,0.1); }
    
    .btn-primary {
      width: 100%; background: var(--crimson); color: #fff; border: none;
      padding: 13px; border-radius: 10px; font-weight: 700; font-size: 14px;
      cursor: pointer; margin-top: 14px; transition: 0.2s;
    }
    .btn-primary:hover { opacity: 0.9; }
    
    input, select {
      width: 100%; background: var(--surface); border: 1px solid var(--border);
      color: #fff; padding: 10px 14px; border-radius: 8px; margin-top: 6px;
      margin-bottom: 12px; font-size: 13px;
    }
  </style>
</head>
<body>

<header>
  <div class="logo">
    <div class="logo-badge">▶</div>
    <span>Dash<span style="color:var(--crimson)">Reels</span></span>
  </div>
  <button class="btn-vip" onclick="openVipModal()">👑 Go Premium VIP</button>
</header>

<div class="container">
  <!-- Monetag Ad Top Banner -->
  <a class="monetag-banner" href="${monetagConfig.globalDirectLinkUrl}" target="_blank">
    <div>
      <span style="background:var(--orange); color:#fff; font-size:9px; font-weight:800; padding:2px 6px; border-radius:4px;">MONETAG</span>
      <strong style="margin-left:8px; font-size:13px;">Sponsored Ad: Click to Stream in 4K Ultra HD & Win Rewards</strong>
    </div>
    <span style="color:var(--gold); font-size:14px; font-weight:700;">Claim ▶</span>
  </a>

  <!-- Main Streaming Player & Episodes Row -->
  <div class="stream-layout">
    <div class="player-card">
      <video id="videoPlayer" controls autoplay playsinline loop>
        <source src="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4" type="video/mp4">
      </video>
    </div>

    <div class="episodes-panel">
      <h3 id="dramaTitle" style="font-size:16px; font-weight:800;">The Twins Love Me Too Late</h3>
      <p style="font-size:11px; color:var(--text-muted); margin-top:4px;">Select an episode to watch or unlock via ad</p>
      
      <div class="episodes-grid" id="episodesContainer">
        <!-- Rendered via JavaScript -->
      </div>
    </div>
  </div>
</div>

<!-- Ad Unlock Modal -->
<div class="modal-overlay" id="adModal">
  <div class="modal-box" style="text-align:center;">
    <button class="close-btn" onclick="closeAdModal()">✕</button>
    <div style="font-size:40px; margin-bottom:10px;">⭐</div>
    <h2 style="font-size:20px; font-weight:800;">Watch Ad to Unlock Episode</h2>
    <p style="font-size:12px; color:var(--text-muted); margin: 8px 0 16px;">Watch a short sponsor ad to unlock this episode for free streaming!</p>
    <div id="adTimerBox" style="display:none; padding:16px; background:var(--surface); border-radius:10px; margin-bottom:14px;">
      <p style="font-weight:700; color:var(--orange);">Playing Monetag Sponsored Ad...</p>
      <p id="countdownText" style="font-size:12px; margin-top:4px;">5 seconds remaining</p>
    </div>
    <button class="btn-primary" id="btnWatchAd" onclick="startWatchingAd()" style="background:var(--orange);">Unlock with Ad (Watch Ad)</button>
    <button class="btn-vip" onclick="closeAdModal(); openVipModal();" style="width:100%; margin-top:10px; padding:12px;">👑 Upgrade to VIP (Ad-Free)</button>
  </div>
</div>

<!-- VIP Payment Modal (bKash & Nagad) -->
<div class="modal-overlay" id="vipModal">
  <div class="modal-box">
    <button class="close-btn" onclick="closeVipModal()">✕</button>
    <h2 style="font-size:18px; font-weight:800;">👑 Premium VIP Subscription</h2>
    <p style="font-size:11px; color:var(--text-muted); margin-bottom:14px;">Ad-free full HD drama & all episodes unlocked</p>

    <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:8px; margin-bottom:14px;">
      <div class="plan-card" onclick="selectPlan('Weekly VIP', '49৳', this)">
        <div style="font-weight:700; font-size:12px;">Weekly</div>
        <div style="color:var(--gold); font-size:15px; font-weight:800;">49৳</div>
      </div>
      <div class="plan-card selected" onclick="selectPlan('Monthly VIP', '149৳', this)">
        <div style="font-size:8px; background:var(--orange); border-radius:3px; padding:1px 3px; display:inline-block;">POPULAR</div>
        <div style="font-weight:700; font-size:12px;">Monthly</div>
        <div style="color:var(--gold); font-size:15px; font-weight:800;">149৳</div>
      </div>
      <div class="plan-card" onclick="selectPlan('Yearly VIP', '999৳', this)">
        <div style="font-weight:700; font-size:12px;">Yearly</div>
        <div style="color:var(--gold); font-size:15px; font-weight:800;">999৳</div>
      </div>
    </div>

    <div style="background:var(--surface); border:1px solid var(--border); border-radius:10px; padding:12px; margin-bottom:12px;">
      <p style="font-size:11px; color:var(--text-muted);">Send Money to bKash / Nagad Number:</p>
      <div style="display:flex; justify-content:space-between; align-items:center; margin-top:4px;">
        <strong style="font-size:16px; color:var(--text);">${paymentConfig.bKashNumber}</strong>
        <span style="font-size:10px; background:var(--gold); color:#000; font-weight:800; padding:2px 6px; border-radius:4px;">Personal</span>
      </div>
      <p style="font-size:10px; color:var(--text-muted); margin-top:4px;">${paymentConfig.instructions}</p>
    </div>

    <label style="font-size:11px; color:var(--text-muted);">Sender Mobile Number:</label>
    <input type="text" id="senderPhone" placeholder="017XXXXXXXX">

    <label style="font-size:11px; color:var(--text-muted);">Transaction ID (TrxID):</label>
    <input type="text" id="trxId" placeholder="BL98K23N09">

    <button class="btn-primary" onclick="submitVipPayment()">Pay & Activate VIP</button>
  </div>
</div>

<script type="module">
  // Connected Firebase Configuration
  import { initializeApp } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-app.js";
  import { getDatabase, ref, set, push, onValue } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-database.js";

  const firebaseConfig = {
    apiKey: "${FirebaseConfigData.API_KEY}",
    authDomain: "${FirebaseConfigData.AUTH_DOMAIN}",
    databaseURL: "${FirebaseConfigData.DATABASE_URL}",
    projectId: "${FirebaseConfigData.PROJECT_ID}",
    storageBucket: "${FirebaseConfigData.STORAGE_BUCKET}",
    messagingSenderId: "${FirebaseConfigData.MESSAGING_SENDER_ID}",
    appId: "${FirebaseConfigData.APP_ID}"
  };

  const app = initializeApp(firebaseConfig);
  const db = getDatabase(app);

  let selectedEp = 1;
  let unlocked = [1, 2, 3];
  let isVip = false;

  window.selectPlan = function(name, price, el) {
    document.querySelectorAll('.plan-card').forEach(c => c.classList.remove('selected'));
    el.classList.add('selected');
  };

  window.openVipModal = function() { document.getElementById('vipModal').style.display = 'flex'; };
  window.closeVipModal = function() { document.getElementById('vipModal').style.display = 'none'; };
  window.openAdModal = function() { document.getElementById('adModal').style.display = 'flex'; };
  window.closeAdModal = function() { document.getElementById('adModal').style.display = 'none'; };

  window.submitVipPayment = function() {
    const phone = document.getElementById('senderPhone').value;
    const trx = document.getElementById('trxId').value;
    if(!phone || !trx) { alert("Please enter both Phone and Transaction ID!"); return; }
    
    // Push order to Firebase Realtime Database
    push(ref(db, 'vip_orders'), {
      phone: phone,
      trxId: trx,
      timestamp: Date.now(),
      status: 'PENDING'
    }).catch(e => console.log('Firebase synced locally:', e));

    alert("Payment submitted! Your VIP status will activate shortly.");
    closeVipModal();
  };

  window.startWatchingAd = function() {
    document.getElementById('adTimerBox').style.display = 'block';
    document.getElementById('btnWatchAd').style.display = 'none';
    let count = 5;
    const interval = setInterval(() => {
      count--;
      document.getElementById('countdownText').innerText = count + ' seconds remaining';
      if(count <= 0) {
        clearInterval(interval);
        unlocked.push(selectedEp);
        closeAdModal();
        playEp(selectedEp);
        renderEpisodes();
        alert('Episode ' + selectedEp + ' unlocked! Enjoy streaming.');
      }
    }, 1000);
  };

  function playEp(num) {
    selectedEp = num;
    const v = document.getElementById('videoPlayer');
    v.src = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
    v.play().catch(()=>{});
    renderEpisodes();
  }

  function renderEpisodes() {
    const container = document.getElementById('episodesContainer');
    container.innerHTML = '';
    for(let i=1; i<=20; i++) {
      const btn = document.createElement('div');
      btn.className = 'ep-btn ' + (selectedEp === i ? 'active' : '');
      const isUnl = isVip || unlocked.includes(i) || i <= 3;
      let badge = '';
      if(!isUnl) {
        badge = i <= 8 ? '<span class="ep-badge ad">AD</span>' : '<span class="ep-badge vip">VIP</span>';
      }
      btn.innerHTML = i + badge;
      btn.onclick = () => {
        if(isUnl) {
          playEp(i);
        } else {
          selectedEp = i;
          openAdModal();
        }
      };
      container.appendChild(btn);
    }
  }

  renderEpisodes();
</script>
</body>
</html>"""
    }
}
