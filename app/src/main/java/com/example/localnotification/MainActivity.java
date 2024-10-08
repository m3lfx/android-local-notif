package com.example.localnotification;

import static androidx.core.content.ContextCompat.getSystemService;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    Button buttonCounter;
    private final static String CHANNEL_ID = "1";
    private final static String CHANNEL_NAME = "1";
    NotificationCompat.Builder builder;
    NotificationManagerCompat compat;
    private final static int NOTIFICATION_ID = 1;
    Integer counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonCounter = findViewById(R.id.buttonCounter);
        buttonCounter.setOnClickListener(v -> {
            counter++;
            buttonCounter.setText(String.valueOf(counter));

            if (counter == 5) {
                sendNotification();
            }
        });


    }

    public void sendNotification() {

        NotificationChannel channel = null;

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent,PendingIntent.FLAG_IMMUTABLE);

        //action button
        Intent actionIntent = new Intent(MainActivity.this,Receiver.class);
        actionIntent.putExtra("toast","This is a notification message");
        PendingIntent actionPending = PendingIntent.getBroadcast(MainActivity.this,1,actionIntent,PendingIntent.FLAG_IMMUTABLE);

        Intent dismissIntent = new Intent(this, DismissReceiver.class);
        PendingIntent dismissPending = PendingIntent.getBroadcast(MainActivity.this,2, dismissIntent,PendingIntent.FLAG_IMMUTABLE);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.android);
        String text = getResources().getString(R.string.big_text);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, MainActivity.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Notification Title")
                .setContentText("Notification Text")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.notification_icon,"Toast Message",actionPending)
                .addAction(R.drawable.notification_icon,"Dismiss",dismissPending)
                .setColor(Color.BLUE)
                .setLargeIcon(bitmap)
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon((Bitmap) null));
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text));

        compat = NotificationManagerCompat.from(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        compat.notify(NOTIFICATION_ID, builder.build());

    }
}