package cz.miklosova.photoeditortm;

import android.widget.SeekBar;
import java.util.Locale;

/**
 * Class BrightnessListener is used on SeekBar element to receive the change of values on it.
 * It uses the new value of brightness to calculate the new thumbnail.
 */
public class BrightnessListener implements SeekBar.OnSeekBarChangeListener {

    /**
     * Method reacts to SeekBar point movement. It computes brightness and uses it to create new thumbnail.
     * Method also changes TextView value on the right side of SeekBar.
     * @param seekBar which SeekBar calls this method
     * @param progress integer value set by the user
     * @param fromUser true if this method is called by this user
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int delta = progress - (seekBar.getMax() / 2);
        // save the brightness value
        TakePhotoActivity.myImage.setBrightness(delta);
        // calculate new thumbnail and set it to imageView
        TakePhotoActivity.imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(
                TakePhotoActivity.myImage.getThumbnailWithSelectedFilter(),
                TakePhotoActivity.myImage.getContrast(), delta));
        TakePhotoActivity.brightnessNumber.setText(String.format(Locale.ENGLISH, "%d", delta));
        TakePhotoActivity.imageView.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


}