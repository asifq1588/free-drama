package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.theme.*
import java.io.File

@Composable
fun AdminHtmlExportScreen(
    htmlCode: String,
    onDownloadClick: (Context, (File?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isDownloading by remember { mutableStateOf(false) }

    fun copyToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("DashReels Full HTML Code", htmlCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Full HTML code copied to clipboard! Ready to paste into Blogger or Web.", Toast.LENGTH_LONG).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 90.dp)
            .testTag("admin_html_export_screen"),
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
                    text = "Download Full HTML Web App",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Standalone HTML/JS code for Blogger, WordPress & Web hosting",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Surface(
                color = EmeraldGreen.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "STANDALONE READY",
                    color = EmeraldGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Summary Card
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Connected to Firebase: dashreels-bfb61", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text("Contains full video player, episode grid, Monetag ads & bKash/Nagad", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                HorizontalDivider(color = SurfaceBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Download Button
                    Button(
                        onClick = {
                            isDownloading = true
                            onDownloadClick(context) { file ->
                                isDownloading = false
                                file?.let { f ->
                                    try {
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            f
                                        )
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/html"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share or Save HTML File"))
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "Saved to ${f.absolutePath}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isDownloading,
                        modifier = Modifier.weight(1f).testTag("download_html_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isDownloading) "Saving..." else "Download HTML", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Copy Code Button
                    Button(
                        onClick = { copyToClipboard() },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("copy_html_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = OledBlack, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy HTML Code", color = OledBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Features list included in the HTML code
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
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("What's Packed Inside This HTML File:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)

                val features = listOf(
                    "🔥 Connected Firebase Realtime Database SDK (dashreels-bfb61)",
                    "🎬 Full 9:16 Vertical Video Reels Cinema Player with autoplay",
                    "📱 Responsive on all devices (Android, iPhone, Desktop & Tablets)",
                    "💰 Monetag Ad direct link banner & 5s rewarded ad countdown modal",
                    "👑 VIP Subscription modal with bKash and Nagad payment gateway",
                    "📝 100% self-contained HTML (No server or npm build required!)"
                )

                features.forEach { feat ->
                    Text(feat, fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        // Code Preview Box
        Text(
            text = "HTML Code Preview (${htmlCode.lines().size} lines):",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextPrimary
        )

        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(10.dp),
            border = ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
        ) {
            Text(
                text = htmlCode.take(2500) + "\n\n/* ... [Tap 'Copy HTML Code' to get full ${htmlCode.length} bytes] ... */",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = AmberGold
            )
        }
    }
}
