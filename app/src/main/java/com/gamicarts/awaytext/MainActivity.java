package com.gamicarts.awaytext;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.ACTION_MAIN;

public class MainActivity extends AppCompatActivity {

    private boolean awayTextOn = false;
    private boolean contactOn = false;
    static final Integer READ = 0x1;
    static final Integer CONTACTS = 0x2;
    static final String NOTIFICATION = "123";
    static final Integer NOTIFICATIONID = 0x3;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms

    void setUp()
    {
        awayTextOn = readInternalFile(MainActivity.this,"awayTextOn");
        contactOn = readInternalFile(MainActivity.this,"contactOn");
        EditText editText = (EditText)findViewById(R.id.awayMessageText);
        editText.setText(readEditTextFile(MainActivity.this,"awayMessage"), TextView.BufferType.EDITABLE);
        createNotificationChannel();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.constraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        setUp();

        final Button awayTextButton = findViewById(R.id.awayTextButton);

        if (awayTextOn)
        {
            awayTextButton.setText("AWAY TEXT: ON");
        }
        else
        {
            awayTextButton.setText("AWAY TEXT: OFF");
        }
        //This will be called when someone clicks awayTextButton
        awayTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awayTextOn)
                {
                    awayTextButton.setText("AWAY TEXT: OFF");
                }
                else
                {
                    awayTextButton.setText("AWAY TEXT: ON");
                    ask(view);
                }

                awayTextOn = !awayTextOn;
                writeInternalFile("awayTextOn", Boolean.toString(awayTextOn));
                createNotification(awayTextOn);
            }
        });

        final Switch textContactsSwitch = findViewById(R.id.textContactsSwitch);
        //This will be called when someone clicks contact switch
        textContactsSwitch.setChecked(contactOn);
        textContactsSwitch.setText("TEXT CONTACTS ONLY: " + (contactOn ? "ON" : "OFF"));

        textContactsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactOn)
                {
                    textContactsSwitch.setText("TEXT CONTACTS ONLY: OFF");
                }
                else
                {
                    textContactsSwitch.setText("TEXT CONTACTS ONLY: ON");
                    ask(view);
                }

                contactOn = !contactOn;
                writeInternalFile("contactOn", Boolean.toString(contactOn));
            }
        });

        EditText editTextStop = (EditText) findViewById(R.id.awayMessageText);
        editTextStop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
                if(timer != null)
                    timer.cancel();
            }
            @Override
            public void afterTextChanged(final Editable s) {
                //avoid triggering event when text is too short
                if (s.length() >= 3) {

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // TODO: do what you need here (refresh list)
                            EditText text = (EditText)findViewById(R.id.awayMessageText);
                            String value = text.getText().toString();
                            writeInternalFile("awayMessage", value);
                        }

                    }, DELAY);
                }
            }
        });

    }

    private void writeInternalFile(String name,String data) {
        String filename = name;
        String fileContents = data;
        File file = new File(MainActivity.this.getFilesDir(), filename);

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.append(fileContents);
            writer.flush();
            writer.close();
            //Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean readInternalFile(Context context, String buttonReader) {
        String yourFilePath = context.getFilesDir() + "/" + buttonReader;

        File yourFile = new File( yourFilePath );
        if (yourFile.exists())
        {
            try {
                BufferedReader br = new BufferedReader(new FileReader(yourFile));
                String info = br.readLine();
                br.close();

                return Boolean.parseBoolean(info);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static String readEditTextFile(Context context, String buttonReader) {
        String yourFilePath = context.getFilesDir() + "/" + buttonReader;

        File yourFile = new File( yourFilePath );
        if (yourFile.exists())
        {
            try {
                BufferedReader br = new BufferedReader(new FileReader(yourFile));
                String info = br.readLine();
                br.close();

                return info;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return "I'm away right now, please leave a message after the beep :)\n- Sent by AwayText";
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Permission Is Granted My Liege
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    // The method is ready to perform requests, and now it should be linked with the corresponding buttons. Each of the buttons created has an android:onClick="ask" property.
    public void ask(View v){
        switch (v.getId()){
            case R.id.awayTextButton:
                askForPermission(Manifest.permission.RECEIVE_SMS, READ);
                askForPermission(Manifest.permission.SEND_SMS, READ);
                break;
            case R.id.textContactsSwitch:
                askForPermission(Manifest.permission.READ_CONTACTS,CONTACTS);
                break;

        }
    }

    //To handle the results of a permission request, the onRequestPermissionsResult method is called.
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            switch (requestCode) {
                //READ
                case 1:
                    //What should we do when we have SMS permission?
                    break;
                case 2:
                    //What should we do with read contact permission
                    break;
            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(boolean on) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setOngoing(true)
                .setShowWhen(false)
                .setContentTitle("A.T.S. is active")
                .setContentText("Tap to manage AwayTextSystem")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Tap to manage AwayTextSystem"))
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (on) {
            notificationManager.notify(NOTIFICATIONID, mBuilder.build());

        } else {
            notificationManager.cancel(NOTIFICATIONID);
        }

    }

}
