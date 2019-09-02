package com.example.photoeditortm;

import android.graphics.Bitmap;
import android.graphics.Matrix;

class MyImage {
    Bitmap fullSizeBitmap;
    Bitmap thumbnail;
    Bitmap thumbnailBackup;
    Bitmap thumbnailWithSelectedFilter;
    int filter;
    int brightness;
    float contrast;

    MyImage(Bitmap bitmap){

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

    Bitmap getThumbnail(){
        return thumbnail;
    }

    Bitmap getFullSizeBitmap(){
        return fullSizeBitmap;
    }

    void setFilter(int i){
        this.filter = i;
    }

}
