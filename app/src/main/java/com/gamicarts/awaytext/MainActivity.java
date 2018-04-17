package com.gamicarts.awaytext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private boolean awayTextOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button awayTextButton = findViewById(R.id.awayTextButton);
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
                }

                awayTextOn = !awayTextOn;
            }
        });
    }

}
