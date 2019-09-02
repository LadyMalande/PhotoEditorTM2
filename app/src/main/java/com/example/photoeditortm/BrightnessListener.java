package com.example.photoeditortm;

import android.widget.SeekBar;

import java.util.Locale;

import static com.example.photoeditortm.TakePhotoActivity.*;

public class BrightnessListener implements SeekBar.OnSeekBarChangeListener {

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

        int delta;
        delta = progress - (seekBar.getMax()/2);
        myImage.brightness=delta;
        imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, myImage.contrast, delta));
        brightnessNumber.setText(String.format(Locale.ENGLISH, "%d", delta));
        imageView.invalidate();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {}

    public void onStopTrackingTouch(SeekBar seekBar) {}

}