package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.example.R

class Widget200MbProvider : AppWidgetProvider() {

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
            val componentName = ComponentName(context, Widget200MbProvider::class.java)
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
            val views = RemoteViews(context.packageName, R.layout.widget_single_200mb)

            // Button 1: Comprar 200 MB por 2 Bs (*10*1*1*1*1#)
            val buyIntent = WidgetActionActivity.createBuyIntent(context)
            val buyPendingIntent = PendingIntent.getActivity(
                context,
                201,
                buyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_buy_200mb, buyPendingIntent)

            // Button 2: Consultar Saldo (*105#)
            val balanceIntent = WidgetActionActivity.createBalanceIntent(context)
            val balancePendingIntent = PendingIntent.getActivity(
                context,
                202,
                balanceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_balance, balancePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
