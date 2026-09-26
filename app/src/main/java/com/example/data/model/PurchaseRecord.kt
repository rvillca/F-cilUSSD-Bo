package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_records")
data class PurchaseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val megasAmount: Int,
    val priceBs: Double,
    val ussdCode: String,
    val validityHours: Int,
    val purchaseTimestamp: Long = System.currentTimeMillis(),
    val expiresTimestamp: Long = System.currentTimeMillis() + (validityHours * 3600 * 1000L),
    val notes: String = ""
) {
    val isCurrentlyActive: Boolean
        get() = System.currentTimeMillis() < expiresTimestamp

    val remainingMillis: Long
        get() = (expiresTimestamp - System.currentTimeMillis()).coerceAtLeast(0L)
}
