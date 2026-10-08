package com.example.util

/**
 * Data representation of a detected SIM card in the device.
 */
data class SimInfo(
    val slotIndex: Int,            // 0-indexed: 0 for SIM 1, 1 for SIM 2
    val subscriptionId: Int,       // Unique system Subscription ID
    val carrierName: String,       // Display or Operator name (e.g., "Entel")
    val mcc: String,               // Mobile Country Code (736 for Bolivia)
    val mnc: String,               // Mobile Network Code (02 / 002 for Entel)
    val isEntel: Boolean,          // Whether this SIM belongs to Entel Bolivia
    val displayName: String        // User-friendly display label (e.g. "SIM 1 (Entel Bolivia)")
)

sealed class OperatorStatus {
    data class EntelFound(val entelSim: SimInfo, val totalSims: Int) : OperatorStatus()
    data class NonEntelFound(val currentCarriers: List<String>) : OperatorStatus()
    object NoSimDetected : OperatorStatus()
    object DetectionPending : OperatorStatus()
}
