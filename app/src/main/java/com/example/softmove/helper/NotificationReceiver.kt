package com.miu.meditationapp.helper

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.softmove.MeditationActivity
import com.example.softmove.R


class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        var repeatingIntent = Intent(context, MeditationActivity::class.java)
        repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        var pendingIntent: PendingIntent = PendingIntent.getActivity(context, 200, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "MORNING")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.about)
            .setContentTitle("Ding!!! ")
            .setContentText("It is time to Meditate! Click here to start meditating.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        var notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(200, builder.build())
    }
}