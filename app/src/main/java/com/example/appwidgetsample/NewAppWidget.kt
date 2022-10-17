package com.example.appwidgetsample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.DateFormat
import java.util.*


/**
 * Implementation of App Widget functionality.
 * Handles the update requests
 */
class NewAppWidget : AppWidgetProvider() {

    /*
    . This method is called the first time the widget runs and again each time
     the widget receives an update request (a broadcast intent).

     Typically involves these tasks:
    -Retrieve any new data that the app widget needs to display.
    -Build a RemoteViews object from the app's context and the app widget's layout file.
    -Update any views within the app widget's layout with new data.
    -Tell the app widget manager to redisplay the widget with the new remote views.

    Every app widget that the user adds to the home screen gets a unique internal ID that
    identifies that app widget.
    * */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    /*
    You would use onEnabled() to perform initial setup for a widget
    (such as opening a new database) when the first instance is initially
    added to the user's home screen. Even if the user adds multiple widgets,
    this method is only called once
    * */
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    /*
     Use onDisabled(), correspondingly, to clean up any resources that were created in onEnabled()
      once the last instance of that widget is removed
    * */
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


/*
    The entire app widget layout must be reconstructed and redisplayed
    each time the widget receives an update intent.
* */
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val prefs = context.getSharedPreferences(
        mSharedPrefFile, 0
    )
    var count = prefs.getInt(COUNT_KEY + appWidgetId, 0)
    count++

    val dateString: String = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)
    views.setTextViewText(R.id.appwidget_id, appWidgetId.toString());

    views.setTextViewText(R.id.appwidget_update,
        context.resources.getString(
            R.string.date_count_format, count, dateString));

    val prefEditor = prefs.edit()
    prefEditor.putInt(COUNT_KEY + appWidgetId, count)
    prefEditor.apply()

    ////Update feature
    val intentUpdate = Intent(context, NewAppWidget::class.java)
    intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    val idArray = intArrayOf(appWidgetId)
    intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
    val pendingUpdate = PendingIntent.getBroadcast(
        context, appWidgetId, intentUpdate,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    views.setOnClickPendingIntent(R.id.button_update, pendingUpdate);


    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private const val mSharedPrefFile = "com.example.android.appwidgetsample"
private const val COUNT_KEY = "count"