package com.example.photoeditortm;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 *
 */
public class About extends AppCompatActivity {
    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView iconsweb = findViewById(R.id.textView_tagofArtist1);
        iconsweb.setMovementMethod(LinkMovementMethod.getInstance());

        TextView myweb = findViewById(R.id.textView_me);
        myweb.setMovementMethod(LinkMovementMethod.getInstance());

        TextView fontweb = findViewById(R.id.textView_fontArtist);
        fontweb.setMovementMethod(LinkMovementMethod.getInstance());

    }

}
