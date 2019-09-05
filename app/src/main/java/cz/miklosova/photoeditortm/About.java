package cz.miklosova.photoeditortm;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


/**
 * This class describes activity called by the user when clicking on button "About" in the MainActivity layout.
 * Its layout gives short info about the app and credits.
 */
public class About extends AppCompatActivity {

    /**
     * This method is called upon creating of this activity
     * @param savedInstanceState tells if there are any variables saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(cz.miklosova
                .photoeditortm.R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(cz.miklosova.photoeditortm.R.layout.activity_about);

        TextView iconsweb = findViewById(cz.miklosova.photoeditortm.R.id.textView_tagofArtist1);
        iconsweb.setMovementMethod(LinkMovementMethod.getInstance());

        TextView myweb = findViewById(cz.miklosova.photoeditortm.R.id.textView_me);
        myweb.setMovementMethod(LinkMovementMethod.getInstance());

        TextView fontweb = findViewById(cz.miklosova.photoeditortm.R.id.textView_fontArtist);
        fontweb.setMovementMethod(LinkMovementMethod.getInstance());

    }

}
