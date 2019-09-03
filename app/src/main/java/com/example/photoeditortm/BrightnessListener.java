package com.example.photoeditortm;

import android.widget.SeekBar;
import java.util.Locale;
import static com.example.photoeditortm.TakePhotoActivity.*;

/**
 * Class BrightnessListener is used on SeekBar element to receive the change of values on it.
 * It uses the new value of brightness to calculate the new thumbnail.
 */
public class BrightnessListener implements SeekBar.OnSeekBarChangeListener {
    /**
     * Method reacts to SeekBar point movement. It computes brightness and uses it to create new thumbnail.
     * Method also changes TextView value on the right side of SeekBar.
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int delta = progress - (seekBar.getMax()/2);
        // save the brightness value
        myImage.brightness=delta;
        // calculate new thumbnail and set it to imageView
        imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, myImage.contrast, delta));
        brightnessNumber.setText(String.format(Locale.ENGLISH, "%d", delta));
        imageView.invalidate();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {}

    public void onStopTrackingTouch(SeekBar seekBar) {}

}