package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.FirebaseConfigData
import com.example.data.model.Drama
import com.example.data.model.Episode
import com.example.ui.theme.*

@Composable
fun AdminFirebaseSetupScreen(
    dramas: List<Drama>,
    episodesMap: Map<String, List<Episode>>,
    isSyncing: Boolean = false,
    onSyncNowClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showJsonDialog by remember { mutableStateOf(false) }

    fun buildJsonBlueprint(): String {
        val dramaJsonList = dramas.map { d ->
            val eps = episodesMap[d.id] ?: emptyList()
            """
    {
      "id": "${d.id}",
      "title": "${d.title}",
      "synopsis": "${d.synopsis.take(40)}...",
      "genres": [${d.genres.joinToString { "\"$it\"" }}],
      "totalEpisodes": ${d.totalEpisodes},
      "episodes": [
        ${eps.joinToString(",\n        ") { e -> 
            """{"episode": ${e.episodeNumber}, "access": "${e.accessType}", "adUrl": "${e.monetagAdUrl}"}"""
        }}
      ]
    }"""
        }
        return "[\n" + dramaJsonList.joinToString(",\n") + "\n]"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 90.dp)
            .testTag("admin_firebase_setup_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Firebase Cloud Integration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Project: ${FirebaseConfigData.PROJECT_ID}",
                    fontSize = 11.sp,
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = EmeraldGreen.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(EmeraldGreen, RoundedCornerShape(3.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CONNECTED",
                        color = EmeraldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Connected Credentials Card
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connected Firebase Credentials:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                }

                val configs = listOf(
                    "Project ID" to FirebaseConfigData.PROJECT_ID,
                    "Database URL" to FirebaseConfigData.DATABASE_URL,
                    "Auth Domain" to FirebaseConfigData.AUTH_DOMAIN,
                    "Storage Bucket" to FirebaseConfigData.STORAGE_BUCKET,
                    "App ID" to FirebaseConfigData.APP_ID,
                    "API Key" to FirebaseConfigData.API_KEY.take(16) + "..."
                )

                configs.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, fontSize = 11.sp, color = TextMuted)
                        Text(value, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // One-click live sync button
                Button(
                    onClick = onSyncNowClick,
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("sync_firebase_button")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Syncing to Firebase...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync Dramas & Episodes to Firebase Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Schema Collections
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Firebase Realtime & Firestore Endpoints:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)

                val collections = listOf(
                    "/dashreels_app/dramas" to "Live Mini-Series catalog with synopsis, genres, and rating",
                    "/dashreels_app/episodes" to "Video streaming URLs with custom Monetag tags",
                    "/vip_orders" to "bKash & Nagad payments and customer TrxIDs",
                    "/monetagConfig" to "Monetag Direct Link & Zone configurations"
                )

                collections.forEach { (path, desc) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceElevated, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(path, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AmberGold)
                        Text(desc, fontSize = 10.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { showJsonDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceHighlight),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("export_firestore_json_button")
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Raw JSON Blueprint", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showJsonDialog) {
        val jsonStr = buildJsonBlueprint()
        AlertDialog(
            onDismissRequest = { showJsonDialog = false },
            title = { Text("Firestore / Realtime Database JSON", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Ready to import into Firebase Console:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        Text(
                            text = jsonStr,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextPrimary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Firestore JSON", jsonStr))
                        Toast.makeText(context, "Copied JSON to clipboard!", Toast.LENGTH_SHORT).show()
                        showJsonDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                ) {
                    Text("Copy JSON", color = OledBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJsonDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = SurfaceElevated
        )
    }
}
