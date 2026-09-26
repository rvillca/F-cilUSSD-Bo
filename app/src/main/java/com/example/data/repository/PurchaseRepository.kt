package com.example.data.repository

import com.example.data.db.PurchaseDao
import com.example.data.model.PurchaseRecord
import kotlinx.coroutines.flow.Flow

class PurchaseRepository(private val purchaseDao: PurchaseDao) {
    val allPurchases: Flow<List<PurchaseRecord>> = purchaseDao.getAllPurchases()
    val latestPurchase: Flow<PurchaseRecord?> = purchaseDao.getLatestPurchase()
    val totalSpentBs: Flow<Double?> = purchaseDao.getTotalSpentBs()

    suspend fun insertPurchase(record: PurchaseRecord): Long {
        return purchaseDao.insertPurchase(record)
    }

    suspend fun deletePurchase(record: PurchaseRecord) {
        purchaseDao.deletePurchase(record)
    }

    suspend fun clearHistory() {
        purchaseDao.clearAll()
    }
}
