package com.example.ui.dialogs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.data.model.EntelPackageCatalog

@Composable
fun AdvancedSettingsDialog(
    isAdvancedMode: Boolean,
    onToggleAdvancedMode: () -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = "Configuración y Depuración",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Modo Simple / Avanzado Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo Desarrollador",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAdvancedMode) "Pestañas de catálogo e historial visibles" else "Modo simple para adulto mayor activo (1 pantalla)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isAdvancedMode,
                        onCheckedChange = { onToggleAdvancedMode() }
                    )
                }

                // Debug USSD codes view
                Text(
                    text = "Comandos USSD del Sistema (Debug):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UssdCodeRow(
                        label = "200 MB (2 Bs):",
                        code = EntelPackageCatalog.STAR_PACKAGE.ussdCode,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(EntelPackageCatalog.STAR_PACKAGE.ussdCode))
                            Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                        }
                    )

                    UssdCodeRow(
                        label = "350 MB + WhatsApp (3 Bs):",
                        code = EntelPackageCatalog.PACKAGE_350MB_WHATSAPP.ussdCode,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(EntelPackageCatalog.PACKAGE_350MB_WHATSAPP.ussdCode))
                            Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                        }
                    )

                    UssdCodeRow(
                        label = "Megas Ilimitado 24h (6 Bs):",
                        code = EntelPackageCatalog.PACKAGE_UNLIMITED_24H.ussdCode,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(EntelPackageCatalog.PACKAGE_UNLIMITED_24H.ussdCode))
                            Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                        }
                    )

                    UssdCodeRow(
                        label = "Activar Doble Carga:",
                        code = EntelPackageCatalog.PROMO_DOBLE_CARGA.ussdCode,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(EntelPackageCatalog.PROMO_DOBLE_CARGA.ussdCode))
                            Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                        }
                    )

                    UssdCodeRow(
                        label = "Consulta de Saldo:",
                        code = "*105#",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString("*105#"))
                            Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                        }
                    )

                    UssdCodeRow(
                        label = "Menú General Entel:",
                        code = "*10#",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString("*10#"))
                            Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("Listo")
            }
        }
    )
}

@Composable
private fun UssdCodeRow(
    label: String,
    code: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = code,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copiar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
