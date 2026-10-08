package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

object TelephonyHelper {

    // Bolivia MCC: 736
    const val MCC_BOLIVIA = "736"
    // Entel Bolivia MNC: 02 or 002
    val MNC_ENTEL_LIST = setOf("02", "002", "2")

    /**
     * Checks if the given MCC, MNC and carrier name belong to Entel Bolivia.
     */
    fun isEntel(mcc: String?, mnc: String?, carrierName: String?): Boolean {
        val cleanMcc = mcc?.trim() ?: ""
        val cleanMnc = mnc?.trim() ?: ""
        val name = carrierName?.trim()?.lowercase() ?: ""

        val mccMatches = cleanMcc == MCC_BOLIVIA
        val mncMatches = cleanMnc in MNC_ENTEL_LIST

        if (mccMatches && mncMatches) return true

        // Fallback checks by carrier name if network code is unavailable
        if (name.contains("entel") && (cleanMcc.isEmpty() || cleanMcc == MCC_BOLIVIA)) {
            return true
        }

        return false
    }

    /**
     * Detects all active SIM cards in the device and checks for Entel Bolivia.
     */
    fun getDetectedSims(context: Context): List<SimInfo> {
        val result = mutableListOf<SimInfo>()

        // 1. Try using SubscriptionManager (standard on Android 5.1+)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                val subList: List<SubscriptionInfo>? = subscriptionManager?.activeSubscriptionInfoList

                if (!subList.isNullOrEmpty()) {
                    for (info in subList) {
                        val mccStr: String
                        val mncStr: String

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            mccStr = info.mccString ?: ""
                            mncStr = info.mncString ?: ""
                        } else {
                            @Suppress("DEPRECATION")
                            mccStr = if (info.mcc > 0) info.mcc.toString() else ""
                            @Suppress("DEPRECATION")
                            mncStr = if (info.mnc > 0) String.format("%02d", info.mnc) else ""
                        }

                        val carrier = info.displayName?.toString()?.ifBlank { null }
                            ?: info.carrierName?.toString()?.ifBlank { null }
                            ?: "SIM ${info.simSlotIndex + 1}"

                        val isEntelSim = isEntel(mccStr, mncStr, carrier)
                        val display = "SIM ${info.simSlotIndex + 1} (${if (isEntelSim) "Entel Bolivia" else carrier})"

                        result.add(
                            SimInfo(
                                slotIndex = info.simSlotIndex,
                                subscriptionId = info.subscriptionId,
                                carrierName = carrier,
                                mcc = mccStr,
                                mnc = mncStr,
                                isEntel = isEntelSim,
                                displayName = display
                            )
                        )
                    }
                    if (result.isNotEmpty()) return result
                }
            } catch (_: Exception) {
                // Ignore and proceed to fallback
            }
        }

        // 2. Fallback to TelephonyManager (works even if READ_PHONE_STATE is not granted)
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            if (telephonyManager != null) {
                val simState = telephonyManager.simState
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    val simOperator = telephonyManager.simOperator ?: "" // e.g. "73602"
                    val mccStr = if (simOperator.length >= 3) simOperator.substring(0, 3) else ""
                    val mncStr = if (simOperator.length > 3) simOperator.substring(3) else ""
                    val carrierName = telephonyManager.simOperatorName?.ifBlank { null }
                        ?: telephonyManager.networkOperatorName?.ifBlank { null }
                        ?: "Operador Móvil"

                    val isEntelSim = isEntel(mccStr, mncStr, carrierName) || simOperator.startsWith("73602")

                    result.add(
                        SimInfo(
                            slotIndex = 0,
                            subscriptionId = 0,
                            carrierName = carrierName,
                            mcc = mccStr,
                            mnc = mncStr,
                            isEntel = isEntelSim,
                            displayName = if (isEntelSim) "SIM 1 (Entel Bolivia)" else "SIM 1 ($carrierName)"
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Ignore failure
        }

        return result
    }

    /**
     * Resolves the current operator status for UI banners and warnings.
     */
    fun evaluateOperatorStatus(context: Context): OperatorStatus {
        val detected = getDetectedSims(context)

        if (detected.isEmpty()) {
            return OperatorStatus.NoSimDetected
        }

        val entelSim = detected.firstOrNull { it.isEntel }
        return if (entelSim != null) {
            OperatorStatus.EntelFound(entelSim, detected.size)
        } else {
            val names = detected.map { it.carrierName }
            OperatorStatus.NonEntelFound(names)
        }
    }

    /**
     * Returns the Entel SimInfo if available, or null.
     */
    fun getEntelSim(context: Context): SimInfo? {
        val sims = getDetectedSims(context)
        return sims.firstOrNull { it.isEntel }
    }
}
