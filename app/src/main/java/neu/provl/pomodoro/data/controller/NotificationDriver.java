package neu.provl.pomodoro.data.controller;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.ThreadLocalRandom;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.SplashScreen;

public class NotificationDriver {

    public static final String NOTIFICATION_CHANNEL_ID = "pomodoro-notification";

    public static void initNotificationChannel() {
        CharSequence name = NOTIFICATION_CHANNEL_ID;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
        NotificationManager notificationManager = SplashScreen.getInstance().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void pushNotification(AppCompatActivity activity, String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_green)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(activity.getApplicationContext())
                .notify((int) (ThreadLocalRandom.current().nextDouble() * 1000), builder.build());
    }
}
