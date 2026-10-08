package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EntelPackage
import com.example.data.model.EntelPackageCatalog
import com.example.data.model.PurchaseRecord
import com.example.ui.components.ActivePackageBanner
import com.example.ui.components.DobleCargaCard
import com.example.ui.components.OperatorStatusBanner
import com.example.ui.components.StarPackageCard
import com.example.ui.dialogs.WidgetInfoModal
import com.example.util.OperatorStatus

@Composable
fun HomeScreen(
    operatorStatus: OperatorStatus,
    latestPurchase: PurchaseRecord?,
    currentTimestamp: Long,
    onBuyPackage: (EntelPackage) -> Unit,
    onCheckBalance: () -> Unit,
    onOpenAboutDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWidgetGuideModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. CARRIER STATUS / DUAL SIM DETECTION BANNER
        OperatorStatusBanner(
            status = operatorStatus,
            onInfoClick = onOpenAboutDialog
        )

        // 2. ACTIVE PACKAGE COUNTDOWN BANNER (Preserved: "04h 26m 03s")
        ActivePackageBanner(
            latestPurchase = latestPurchase,
            currentTimestamp = currentTimestamp
        )

        // 3. MAIN HERO CARD: 200 MB, 350 MB + WA & ILIMITADO SELECTOR WITH HUGE BUTTON
        StarPackageCard(
            onBuyPackage = onBuyPackage
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 4. DOBLE CARGA PROMOTION CARD (*10*4*1#)
        DobleCargaCard(
            onActivateDobleCarga = {
                onBuyPackage(EntelPackageCatalog.PROMO_DOBLE_CARGA)
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 5. SECONDARY ACTION: CONSULTAR SALDO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(22.dp))
                .clickable(onClick = onCheckBalance),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Consultar mi Saldo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ver crédito disponible en la línea",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Consultar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. HELPER ACTION: WIDGET INSTRUCTIONS MODAL LINK
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .clickable { showWidgetGuideModal = true }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Cómo poner el botón en la pantalla de inicio",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 7. MANDATORY GOOGLE PLAY LEGAL DISCLAIMER FOOTER
        Text(
            text = stringResource(R.string.disclaimer_text),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            lineHeight = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .clickable { onOpenAboutDialog() }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showWidgetGuideModal) {
        WidgetInfoModal(onDismiss = { showWidgetGuideModal = false })
    }
}
