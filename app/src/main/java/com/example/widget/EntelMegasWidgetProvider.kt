package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.example.R

class EntelMegasWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, EntelMegasWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            for (appWidgetId in appWidgetIds) {
                updateWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_entel_megas)

            // Button 1: Comprar 200 MB por 2 Bs (*10*1*1*1*1#)
            val buyIntent = WidgetActionActivity.createBuyIntent(context)
            val buyPendingIntent = PendingIntent.getActivity(
                context,
                101,
                buyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_buy_200mb, buyPendingIntent)

            // Button 2: Comprar 350 MB + WhatsApp por 3 Bs (*10*1*1*2*1#)
            val buy350Intent = WidgetActionActivity.createBuy350MbWaIntent(context)
            val buy350PendingIntent = PendingIntent.getActivity(
                context,
                102,
                buy350Intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_buy_350mb, buy350PendingIntent)

            // Button 3: Comprar Ilimitado 24h por 6 Bs (*10*1*3*1*1#)
            val buyUnlimitedIntent = WidgetActionActivity.createBuyUnlimitedIntent(context)
            val buyUnlimitedPendingIntent = PendingIntent.getActivity(
                context,
                103,
                buyUnlimitedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_buy_unlimited, buyUnlimitedPendingIntent)

            // Button 4: Activar Doble Carga Mensual (*10*4*1#)
            val dobleCargaIntent = WidgetActionActivity.createDobleCargaIntent(context)
            val dobleCargaPendingIntent = PendingIntent.getActivity(
                context,
                104,
                dobleCargaIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_doble_carga, dobleCargaPendingIntent)

            // Button 5: Consultar Saldo (*105#)
            val balanceIntent = WidgetActionActivity.createBalanceIntent(context)
            val balancePendingIntent = PendingIntent.getActivity(
                context,
                105,
                balanceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_balance, balancePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
