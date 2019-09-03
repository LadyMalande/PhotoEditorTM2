package com.example.photoeditortm;

import android.widget.SeekBar;
import static com.example.photoeditortm.TakePhotoActivity.*;

/**
 * Class ContrastListener is used on SeekBar element to receive the change of values on it.
 * It uses the new value of contrast to calculate the new thumbnail.
 */
public class ContrastListener implements SeekBar.OnSeekBarChangeListener {
    /**
     * Method reacts to SeekBar point movement. It computes contrast and uses it to create new thumbnail.
     * Method also changes TextView value on the right side of SeekBar.
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // calculate new thumbnail and set it to imageView
        imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, (float) progress / 100f, myImage.brightness));
        contrastNumber.setText(Float.toHexString(progress / 100f) );
        imageView.invalidate();
        // save the contrast value
        myImage.contrast=progress / 100f;
    }

    public void onStartTrackingTouch(SeekBar seekBar) {}

    public void onStopTrackingTouch(SeekBar seekBar) {}

}
