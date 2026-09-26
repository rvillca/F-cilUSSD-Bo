package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PurchaseRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchase_records ORDER BY purchaseTimestamp DESC")
    fun getAllPurchases(): Flow<List<PurchaseRecord>>

    @Query("SELECT * FROM purchase_records ORDER BY purchaseTimestamp DESC LIMIT 1")
    fun getLatestPurchase(): Flow<PurchaseRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(record: PurchaseRecord): Long

    @Delete
    suspend fun deletePurchase(record: PurchaseRecord)

    @Query("DELETE FROM purchase_records")
    suspend fun clearAll()

    @Query("SELECT SUM(priceBs) FROM purchase_records")
    fun getTotalSpentBs(): Flow<Double?>
}
