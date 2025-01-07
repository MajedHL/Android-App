package fr.utt.if26.tasksorganizer.Utils;


import static fr.utt.if26.tasksorganizer.Utils.NotificationSystem.getChannelId;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import fr.utt.if26.tasksorganizer.Activities.TodayActivity;
import fr.utt.if26.tasksorganizer.R;

public class AlarmReceiver extends BroadcastReceiver {
    int NOTIFICATION_ID = 123456;


    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName= intent.getStringExtra("taskName");
        Integer taskId = intent.getIntExtra("taskId",NOTIFICATION_ID);

        // Show notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, getChannelId())
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle("TaskOrganizer Reminder")
                        .setContentText(taskName)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Intent intent1 = new Intent(context, TodayActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
            return;
        }
        notificationManager.notify(taskId, builder.build());
    }
}
