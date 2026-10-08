package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EntelPackage
import com.example.data.model.EntelPackageCatalog
import com.example.ui.theme.GoogleBlueDarkText
import com.example.ui.theme.GooglePastelBlue

@Composable
fun StarPackageCard(
    onBuyPackage: (EntelPackage) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // 0: 200 MB (2 Bs), 1: 350 MB + WA (3 Bs), 2: Ilimitado (6 Bs)
    var selectedPackageIndex by remember { mutableIntStateOf(0) }
    val currentPackage = when (selectedPackageIndex) {
        1 -> EntelPackageCatalog.PACKAGE_350MB_WHATSAPP
        2 -> EntelPackageCatalog.PACKAGE_UNLIMITED_24H
        else -> EntelPackageCatalog.STAR_PACKAGE
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ACCESSIBLE 3-OPTION SELECTOR (Large touch targets for seniors)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PackageOptionChip(
                    label = "200 MB • 2 Bs",
                    isSelected = selectedPackageIndex == 0,
                    onClick = { selectedPackageIndex = 0 },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                PackageOptionChip(
                    label = "350 MB • 3 Bs",
                    isSelected = selectedPackageIndex == 1,
                    onClick = { selectedPackageIndex = 1 },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                PackageOptionChip(
                    label = "Ilimitado • 6 Bs",
                    isSelected = selectedPackageIndex == 2,
                    onClick = { selectedPackageIndex = 2 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // DYNAMIC LARGE DIGIT DISPLAY (Senior-friendly, instant feedback)
            AnimatedContent(
                targetState = selectedPackageIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "package_display"
            ) { index ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (index) {
                        1 -> {
                            // 350 MB + WA
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "350",
                                    fontSize = 54.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 54.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MB",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Chat,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "+ WhatsApp",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Precio: 3 Bolivianos • 24 Horas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        2 -> {
                            // Ilimitado 24h
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AllInclusive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(46.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ILIMITADO",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Precio: 6 Bolivianos • Todo el día sin límite",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        else -> {
                            // 200 MB (Default)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "200",
                                    fontSize = 58.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 58.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "MB",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            Text(
                                text = "Precio exacto: 2 Bolivianos • 24 Horas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // THE MASSIVE PRIMARY BUTTON (76dp height, high contrast, senior friendly)
            Button(
                onClick = { onBuyPackage(currentPackage) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .testTag("star_package_buy_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GooglePastelBlue,
                    contentColor = GoogleBlueDarkText
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoogleBlueDarkText),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = GooglePastelBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        val buttonTitle = when (selectedPackageIndex) {
                            1 -> "COMPRAR 350 MB (3 Bs)"
                            2 -> "COMPRAR ILIMITADO (6 Bs)"
                            else -> "COMPRAR 200 MB (2 Bs)"
                        }
                        val buttonSubtitle = when (selectedPackageIndex) {
                            1 -> "Incluye WhatsApp libre por 24 horas"
                            2 -> "Internet sin límite por 24 horas"
                            else -> "Toca aquí para comprar por 2 Bs"
                        }
                        Text(
                            text = buttonTitle,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = GoogleBlueDarkText,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = buttonSubtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoogleBlueDarkText.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PackageOptionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
