package com.example.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
     * Uses ACTION_CALL if permission is granted, otherwise ACTION_DIAL.
     * @return true if ACTION_CALL was used, false if ACTION_DIAL fallback was used.
     */
    fun executeUssd(context: Context, ussdCode: String, showToast: Boolean = true): Boolean {
        val encodedCode = Uri.encode(ussdCode)
        val uri = Uri.parse("tel:$encodedCode")

        return try {
            if (hasCallPermission(context)) {
                val callIntent = Intent(Intent.ACTION_CALL, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(callIntent)
                if (showToast) {
                    Toast.makeText(context, "Llamando USSD $ussdCode...", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                val dialIntent = Intent(Intent.ACTION_DIAL, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(dialIntent)
                if (showToast) {
                    Toast.makeText(context, "Abriendo marcador con $ussdCode", Toast.LENGTH_SHORT).show()
                }
                false
            }
        } catch (e: Exception) {
            // Fallback to DIAL
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(dialIntent)
                false
            } catch (err: Exception) {
                if (showToast) {
                    Toast.makeText(context, "Error al iniciar USSD: ${err.localizedMessage}", Toast.LENGTH_LONG).show()
                }
                false
            }
        }
    }
}
