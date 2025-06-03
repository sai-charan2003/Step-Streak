package com.charan.stepstreak.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.charan.stepstreak.MainActivity
import com.charan.stepstreak.R
import com.charan.stepstreak.data.repository.DataStoreRepo
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    private val context : Context,
) {
    private val notificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    companion object {
        private const val STEP_MILESTONES_CHANNEL_ID = "step-streak-milestone-notifications"
        private const val STEP_MILESTONES_CHANNEL_NAME = "Step Milestones"
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun showStepMilestoneNotification(stepsPercentage: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val (title, message) = when (stepsPercentage) {
            25 -> Constants.milestone25Notifications.random()
            50 -> Constants.milestone50Notifications.random()
            75 -> Constants.milestone75Notifications.random()
            100 -> Constants.milestone100Notifications.random()
            else -> return
        }

        val notificationBuilder = Notification.Builder(context, STEP_MILESTONES_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.applogo)
            .setAutoCancel(true)
            .setProgress(100, stepsPercentage, false)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        val headPhonesChannel = NotificationChannel(
            STEP_MILESTONES_CHANNEL_ID,
            STEP_MILESTONES_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannels(listOf(headPhonesChannel))
    }

    fun isPermissionGranted(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }


}