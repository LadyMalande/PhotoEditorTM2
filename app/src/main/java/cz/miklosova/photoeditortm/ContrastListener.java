package cz.miklosova.photoeditortm;

import android.widget.SeekBar;
import static cz.miklosova.photoeditortm.TakePhotoActivity.*;

/**
 * Class ContrastListener is used on SeekBar element to receive the change of values on it.
 * It uses the new value of contrast to calculate the new thumbnail.
 */
public class ContrastListener implements SeekBar.OnSeekBarChangeListener {

    /**
     * Method reacts to SeekBar point movement. It computes contrast and uses it to create new thumbnail.
     * Method also changes TextView value on the right side of SeekBar.
     * @param seekBar which SeekBar calls this method
     * @param progress integer value set  by the user
     * @param fromUser true if this method is called by this user
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // calculate new thumbnail and set it to imageView
       imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(
                myImage.getThumbnailWithSelectedFilter(),
                (float) progress / 100f, myImage.getBrightness()));
        contrastNumber.setText(Float.toString(progress / 100f));
        imageView.invalidate();
        // save the contrast value
        myImage.setContrast(progress / 100f);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

}
