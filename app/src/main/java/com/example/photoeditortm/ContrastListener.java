package com.example.photoeditortm;

import android.widget.SeekBar;

import static com.example.photoeditortm.TakePhotoActivity.*;

public class ContrastListener implements SeekBar.OnSeekBarChangeListener {

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, (float) progress / 100f, myImage.brightness));
        contrastNumber.setText(""+(float) progress / 100f);
        imageView.invalidate();
        myImage.contrast=progress / 100f;
    }

    public void onStartTrackingTouch(SeekBar seekBar) {}

    public void onStopTrackingTouch(SeekBar seekBar) {}

}
