package com.gamicarts.awaytext;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private boolean awayTextOn = false;
    private boolean contactOn = false;
    static final Integer READ = 0x1;
    static final Integer CONTACTS = 0x2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button awayTextButton = findViewById(R.id.awayTextButton);
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
            }
        });

        final Switch textContactsSwitch = findViewById(R.id.textContactsSwitch);
        //This will be called when someone clicks awayTextButton
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
            }
        });
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
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    // The method is ready to perform requests, and now it should be linked with the corresponding buttons. Each of the buttons created has an android:onClick="ask" property.
    public void ask(View v){
        switch (v.getId()){
            case R.id.awayTextButton:
                askForPermission(Manifest.permission.READ_SMS,READ);
                askForPermission(Manifest.permission.SEND_SMS,READ);
                askForPermission(Manifest.permission.RECEIVE_SMS,READ);
                //askForPermission(Manifest.permission.WRITE_SMS,READ);

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

}
