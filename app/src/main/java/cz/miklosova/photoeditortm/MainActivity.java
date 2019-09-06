package cz.miklosova.photoeditortm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class is the one that starts the app. It provides just two buttons.
 * One to get to photo editor and one to read credits of the app.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button) Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(cz.miklosova.photoeditortm.R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(cz.miklosova.photoeditortm.R.layout.activity_main);

        //set Listener on button1 for starting new Activity
        ButterKnife.bind(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity2Intent = new Intent(getApplicationContext(), TakePhotoActivity.class);
                startActivity(activity2Intent);
            }
        });
    }

    public void startAboutActivity(View v) {
        Intent activity3Intent = new Intent(getApplicationContext(), About.class);
        startActivity(activity3Intent);
    }
}
