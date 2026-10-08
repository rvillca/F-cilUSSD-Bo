package com.example.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.core.content.ContextCompat

object UssdHelper {

    fun hasCallPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Executes the USSD code.
     * Correctly encodes the '#' symbol to '%23' using Uri.encode.
     * In Dual SIM devices, routes the call to the specified Entel SIM subscription.
     * Uses ACTION_CALL when permission is granted, otherwise falls back gracefully to ACTION_DIAL.
     */
    fun executeUssd(
        context: Context,
        ussdCode: String,
        targetSim: SimInfo? = null,
        showToast: Boolean = true
    ): Boolean {
        // Safe encoding: ensures *105# -> tel:*105%23
        val encodedCode = Uri.encode(ussdCode)
        val uri = Uri.parse("tel:$encodedCode")

        val hasPermission = hasCallPermission(context)
        val action = if (hasPermission) Intent.ACTION_CALL else Intent.ACTION_DIAL

        val intent = Intent(action, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Inject Dual SIM routing extras for manufacturer compatibility
            if (targetSim != null) {
                putExtra("subscription_id", targetSim.subscriptionId)
                putExtra("simSlot", targetSim.slotIndex)
                putExtra("com.android.phone.extra.slot", targetSim.slotIndex)
                putExtra("slot", targetSim.slotIndex)
                putExtra("simId", targetSim.subscriptionId)

                // Attach TelecomManager PhoneAccountHandle on Android M+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                        val accounts = telecomManager?.callCapablePhoneAccounts
                        if (!accounts.isNullOrEmpty()) {
                            for (account in accounts) {
                                if (account.id.contains(targetSim.subscriptionId.toString()) ||
                                    account.id.contains(targetSim.slotIndex.toString())) {
                                    putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, account)
                                    break
                                }
                            }
                        }
                    } catch (_: Exception) {
                        // Ignore telecom lookup failures
                    }
                }
            }
        }

        return try {
            context.startActivity(intent)
            if (showToast) {
                val simLabel = if (targetSim != null) " (SIM ${targetSim.slotIndex + 1})" else ""
                val msg = if (hasPermission) "Marcando USSD en línea Entel$simLabel..." else "Abriendo marcador..."
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            hasPermission
        } catch (e: Exception) {
            // Graceful fallback to ACTION_DIAL on any failure
            try {
                val fallbackIntent = Intent(Intent.ACTION_DIAL, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
                false
            } catch (err: Exception) {
                if (showToast) {
                    Toast.makeText(context, "No fue posible abrir el marcador.", Toast.LENGTH_LONG).show()
                }
                false
            }
        }
    }
}
