package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddCustomPackageDialog(
    onAdd: (title: String, megasAmount: Int, priceBs: Double, hours: Int, ussdCode: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var megasStr by remember { mutableStateOf("200") }
    var priceStr by remember { mutableStateOf("2") }
    var hoursStr by remember { mutableStateOf("24") }
    var ussdCode by remember { mutableStateOf("*10*1*1*1*1#") }

    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nuevo Paquete Personalizado",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre o Descripción") },
                    placeholder = { Text("Ej: Promo Noche Megas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = ussdCode,
                    onValueChange = { ussdCode = it },
                    label = { Text("Código USSD") },
                    placeholder = { Text("*10*1*1*1*1#") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = megasStr,
                    onValueChange = { megasStr = it },
                    label = { Text("Cantidad en Megabytes (MB)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Precio en Bolivianos (Bs)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = hoursStr,
                    onValueChange = { hoursStr = it },
                    label = { Text("Vigencia (Horas)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (isError) {
                    Text(
                        text = "Por favor completa los campos correctamente",
                        color = androidx.compose.ui.graphics.Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val megas = megasStr.toIntOrNull() ?: 0
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val hours = hoursStr.toIntOrNull() ?: 24
                    val validCode = ussdCode.trim()

                    if (title.isNotBlank() && validCode.isNotBlank()) {
                        onAdd(title.trim(), megas, price, hours, validCode)
                    } else {
                        isError = true
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Paquete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
