package com.example.photoeditortm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set Listener on button1 for starting new Activity with photo value
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activity2Intent = new Intent(getApplicationContext(), TakePhotoActivity.class);
                startActivity(activity2Intent);
            }
        });
/*
        Button button_about = findViewById(R.id.button_about);
        button_about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activity3Intent = new Intent(getApplicationContext(), About.class);
                startActivity(activity3Intent);
            }
        });
*/

    }

    public void startAboutActivity(View v){
        Intent activity3Intent = new Intent(getApplicationContext(), About.class);
        startActivity(activity3Intent);
    }
}
