package com.example.photoeditortm;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class MyImage {
    public Bitmap fullSizeBitmap;
    public Bitmap thumbnail;
    public Bitmap thumbnailBackup;
    public Bitmap thumbnailWithSelectedFilter;
    public int filter;
    public int brightness;
    public float contrast;

    public MyImage(Bitmap bitmap){

        this.fullSizeBitmap = bitmap;
        int maxHeight = TakePhotoActivity.getSizeHeightImageView();
        int maxWidth = TakePhotoActivity.getSizeWidthImageView();
        float scale = Math.min(((float)maxHeight / bitmap.getWidth()), ((float)maxWidth / bitmap.getHeight()));

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        this.thumbnail= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        this.thumbnailWithSelectedFilter = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
         this.thumbnailBackup =  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        this.filter = 0;
        this.brightness = 0;
        this.contrast = 1;

    }

    public Bitmap getThumbnail(){
        return thumbnail;
    }

    public Bitmap getFullSizeBitmap(){
        return fullSizeBitmap;
    }

    public void setFilter(int i){
        this.filter = i;
    }

}
