package com.example.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.data.db.AppDatabase
import com.example.data.model.EntelPackageCatalog
import com.example.data.model.PurchaseRecord
import com.example.util.UssdHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetActionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent?.action
        when (action) {
            ACTION_BUY_200MB -> {
                val star = EntelPackageCatalog.STAR_PACKAGE
                val record = PurchaseRecord(
                    packageName = "${star.megasLabel} (${star.priceBs.toInt()} Bs)",
                    megasAmount = star.megasAmount,
                    priceBs = star.priceBs,
                    ussdCode = star.ussdCode,
                    validityHours = star.validityHours,
                    notes = "Comprado desde Widget de Inicio"
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(applicationContext)
                    db.purchaseDao().insertPurchase(record)
                    EntelMegasWidgetProvider.updateAllWidgets(applicationContext)
                }

                Toast.makeText(
                    this,
                    "⚡ Comprando 200 MB por 2 Bs (*10*1*1*1*1#)...",
                    Toast.LENGTH_LONG
                ).show()

                UssdHelper.executeUssd(this, star.ussdCode, showToast = false)
            }
            ACTION_CHECK_BALANCE -> {
                Toast.makeText(this, "Consultando saldo (*105#)...", Toast.LENGTH_SHORT).show()
                UssdHelper.executeUssd(this, "*105#", showToast = false)
            }
            ACTION_OPEN_MENU -> {
                Toast.makeText(this, "Abriendo menú Entel (*10#)...", Toast.LENGTH_SHORT).show()
                UssdHelper.executeUssd(this, "*10#", showToast = false)
            }
        }

        finish()
    }

    companion object {
        const val ACTION_BUY_200MB = "com.example.widget.ACTION_BUY_200MB"
        const val ACTION_CHECK_BALANCE = "com.example.widget.ACTION_CHECK_BALANCE"
        const val ACTION_OPEN_MENU = "com.example.widget.ACTION_OPEN_MENU"

        fun createBuyIntent(context: Context): Intent {
            return Intent(context, WidgetActionActivity::class.java).apply {
                action = ACTION_BUY_200MB
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        fun createBalanceIntent(context: Context): Intent {
            return Intent(context, WidgetActionActivity::class.java).apply {
                action = ACTION_CHECK_BALANCE
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        fun createMenuIntent(context: Context): Intent {
            return Intent(context, WidgetActionActivity::class.java).apply {
                action = ACTION_OPEN_MENU
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
