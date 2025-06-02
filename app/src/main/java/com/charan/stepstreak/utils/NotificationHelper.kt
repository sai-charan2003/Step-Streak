package com.charan.stepstreak.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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

    fun showStepMilestoneNotification(stepsPercentage: Int) {
        val (title, message) = when (stepsPercentage) {
            25 -> Constants.milestone25Notifications.random()
            50 -> Constants.milestone50Notifications.random()
            75 -> Constants.milestone75Notifications.random()
            100 -> Constants.milestone100Notifications.random()
            else -> return
        }

        val notificationBuilder = android.app.Notification.Builder(context, STEP_MILESTONES_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.applogo)
            .setAutoCancel(true)
            .setProgress(100, stepsPercentage, false)

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