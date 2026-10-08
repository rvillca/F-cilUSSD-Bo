package com.example.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AppDatabase
import com.example.data.model.EntelPackage
import com.example.data.model.EntelPackageCatalog
import com.example.data.model.PurchaseRecord
import com.example.ui.theme.GoogleBlueDarkText
import com.example.ui.theme.GooglePastelBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.util.SimInfo
import com.example.util.TelephonyHelper
import com.example.util.UssdHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetActionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val entelSim = TelephonyHelper.getEntelSim(this)
        val action = intent?.action

        val targetPackage = when (action) {
            ACTION_BUY_200MB -> EntelPackageCatalog.STAR_PACKAGE
            ACTION_BUY_350MB_WA -> EntelPackageCatalog.PACKAGE_350MB_WHATSAPP
            ACTION_BUY_UNLIMITED -> EntelPackageCatalog.PACKAGE_UNLIMITED_24H
            ACTION_ACTIVATE_DOBLE_CARGA -> EntelPackageCatalog.PROMO_DOBLE_CARGA
            else -> null
        }

        val isBalanceCheck = action == ACTION_CHECK_BALANCE

        if (targetPackage == null && !isBalanceCheck) {
            finish()
            return
        }

        setContent {
            MyApplicationTheme {
                var isDialogOpen by remember { mutableStateOf(true) }

                if (isDialogOpen) {
                    if (targetPackage != null) {
                        WidgetConfirmationDialog(
                            pkg = targetPackage,
                            simInfo = entelSim,
                            onConfirm = {
                                isDialogOpen = false
                                executePackagePurchase(targetPackage, entelSim)
                                finish()
                            },
                            onDismiss = {
                                isDialogOpen = false
                                finish()
                            }
                        )
                    } else if (isBalanceCheck) {
                        WidgetBalanceDialog(
                            simInfo = entelSim,
                            onConfirm = {
                                isDialogOpen = false
                                Toast.makeText(this@WidgetActionActivity, "Consultando saldo (*105#)...", Toast.LENGTH_SHORT).show()
                                UssdHelper.executeUssd(this@WidgetActionActivity, "*105#", targetSim = entelSim, showToast = false)
                                finish()
                            },
                            onDismiss = {
                                isDialogOpen = false
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun executePackagePurchase(pkg: EntelPackage, entelSim: SimInfo?) {
        if (pkg.priceBs > 0) {
            val record = PurchaseRecord(
                packageName = "${pkg.megasLabel} (${pkg.priceBs.toInt()} Bs)",
                megasAmount = pkg.megasAmount,
                priceBs = pkg.priceBs,
                ussdCode = pkg.ussdCode,
                validityHours = pkg.validityHours,
                notes = "Comprado desde Widget con confirmación"
            )

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(applicationContext)
                db.purchaseDao().insertPurchase(record)
                EntelMegasWidgetProvider.updateAllWidgets(applicationContext)
            }

            Toast.makeText(
                this,
                "Marcando ${pkg.title} por ${pkg.priceBs.toInt()} Bs...",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Activando ${pkg.title}...",
                Toast.LENGTH_LONG
            ).show()
        }

        UssdHelper.executeUssd(this, pkg.ussdCode, targetSim = entelSim, showToast = false)
    }

    companion object {
        const val ACTION_BUY_200MB = "com.example.widget.ACTION_BUY_200MB"
        const val ACTION_BUY_350MB_WA = "com.example.widget.ACTION_BUY_350MB_WA"
        const val ACTION_BUY_UNLIMITED = "com.example.widget.ACTION_BUY_UNLIMITED"
        const val ACTION_ACTIVATE_DOBLE_CARGA = "com.example.widget.ACTION_ACTIVATE_DOBLE_CARGA"
        const val ACTION_CHECK_BALANCE = "com.example.widget.ACTION_CHECK_BALANCE"

        fun createBuyIntent(context: Context): Intent {
            return Intent(context, WidgetActionActivity::class.java).apply {
                action = ACTION_BUY_200MB
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        fun createBuy350MbWaIntent(context: Context): Intent {
            return Intent(context, WidgetActionActivity::class.java).apply {
                action = ACTION_BUY_350MB_WA
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        fun createBuyUnlimitedIntent(context: Context): Intent {
            return Intent(context, WidgetActionActivity::class.java).apply {
                action = ACTION_BUY_UNLIMITED
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        fun createDobleCargaIntent(context: Context): Intent {
            return Intent(context, WidgetActionActivity::class.java).apply {
                action = ACTION_ACTIVATE_DOBLE_CARGA
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        fun createBalanceIntent(context: Context): Intent {
            return Intent(context, WidgetActionActivity::class.java).apply {
                action = ACTION_CHECK_BALANCE
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}

@Composable
fun WidgetConfirmationDialog(
    pkg: EntelPackage,
    simInfo: SimInfo?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDobleCarga = pkg.id == "promo_doble_carga"

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isDobleCarga) Color(0xFFFFE082) else GooglePastelBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDobleCarga) Icons.Default.CardGiftcard else Icons.Default.Bolt,
                    contentDescription = null,
                    tint = if (isDobleCarga) Color(0xFF5D4037) else GoogleBlueDarkText,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = if (isDobleCarga) "¿Activar Doble Carga?" else "¿Confirmar Compra de Megas?",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Has presionado el botón del widget. ¿Deseas continuar?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Package Details Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = pkg.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (pkg.priceBs > 0) {
                            Text(
                                text = "Precio: ${pkg.priceBs.toInt()} Bolivianos • Vigencia: ${pkg.validityLabel}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Beneficio Entel • ${pkg.validityLabel}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // SIM info
                        if (simInfo != null) {
                            Text(
                                text = "Línea detectada: ${simInfo.displayName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GooglePastelBlue,
                    contentColor = GoogleBlueDarkText
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isDobleCarga) "Sí, Activar" else "Sí, Comprar",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun WidgetBalanceDialog(
    simInfo: SimInfo?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = "¿Consultar Saldo?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "¿Deseas consultar el saldo disponible de tu línea móvil?",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (simInfo != null) {
                    Text(
                        text = "Línea: ${simInfo.displayName}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("Consultar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}
