package com.capstone_project.medispot.HelperClasses;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.capstone_project.medispot.CommonFiles.LoginSignup.Login;
import com.capstone_project.medispot.R;

public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {

        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Intent open = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,open, PendingIntent.FLAG_IMMUTABLE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "android")
        .setSmallIcon(R.drawable.medispot)
        .setContentTitle("Reminder")
        .setContentText("Take your medications on time!!")
        .setAutoCancel(true)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setSound(alarmSound)
        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());

        CountDownTimer timer = new CountDownTimer(10000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Nothing to do
            }

            @Override
            public void onFinish() {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        };
        timer.start();
    }
}
