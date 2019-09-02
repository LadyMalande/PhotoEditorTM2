package com.example.photoeditortm;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;


class Filters {

        private static int w;
        private static int h;
        private static int[] pix;

    /**
         * Sets width of an image for computation purposes in some filters.
     * @param WS Width of the image.
     */
        private static void setW(int WS){
            w = WS;
        }

        /**
         * Sets height of an image for computation purposes in some filters.
         * @param HS Height of the image.
         */
        private static void setH(int HS){
            h = HS;
        }

        /**
         * used to prevent unwanted numbers in RGB color scheme
         */
        private static int flatten(int i) {
            if(i > 255){
                return 255;
            } else if(i < 0) {
                return 0;
            } else {
                return i;
            }
        }


    static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {

        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    static Bitmap polaroid(Bitmap bmp) {

        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1.438f, -0.062f, -0.062f,0, 0,
                        -0.122f, 1.378f, -0.122f, 0, 0,
                        -0.016f, -0.016f, 1.483f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        TakePhotoActivity.myImage.setFilter(4);
        return ret;
    }
    /*
        public static Bitmap burn(Bitmap bitmap,int HowMuch) {

        changeOrder(1);



            setH(bitmap.getHeight());
            setW(bitmap.getWidth());
            pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);
            Bitmap bit = Bitmap.createBitmap(bitmap);
            int index, r,g,b;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    index = y * w + x;
                    r = ((pix[index] >> 16) & 0xff )+ HowMuch;
                    g = ((pix[index] >> 8) & 0xff )+ HowMuch;
                    b = ( pix[index] & 0xff )+ HowMuch;

                    pix[index] = 0xff000000 | (flatten(r) << 16) | (flatten(g) << 8) | flatten(b);
                }
            }

                bit.setPixels(pix, 0, w, 0, 0, w, h);


            if(TakePhotoActivity.myImage.contrast != 0 && doNotRepeat != 1){
                doNotRepeat = 1;
                bit=contrast(bit, TakePhotoActivity.myImage.contrast);
            }


            doNotRepeat = 0;

            return bit;
        }
*/
   /*
    public static Bitmap contrast(Bitmap bitmap,int HowMuch) {


            if(TakePhotoActivity.myImage.brightness != 0 && doNotRepeat != 1){
                doNotRepeat = 1;
                bitmap=burn(bitmap, TakePhotoActivity.myImage.brightness);
            }

        changeOrder(2);
        setH(bitmap.getHeight());
        setW(bitmap.getWidth());
        pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        Bitmap bit = Bitmap.createBitmap(bitmap);
        int index, r,g,b;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                index = y * w + x;
                //int total = ((pix[index] >> 16) & 0xff ) + ((pix[index] >> 8) & 0xff ) + ( pix[index] & 0xff );
                //int multiplier;
               /* if(total < 383){
                    multiplier = -1;
                }
                else{
                    multiplier = 1;
                }

                r = ((pix[index] >> 16) & 0xff );
                g = ((pix[index] >> 8) & 0xff );
                b = ( pix[index] & 0xff );
                if(r <= 127){
                    r = r + (HowMuch * -1);
                }
                else{
                    r = r + HowMuch;
                }
                if(g <= 127){
                    g = g + (HowMuch * -1);
                }
                else{
                    g = g + HowMuch;
                }
                if(b <= 127){
                    b = b + (HowMuch * -1);
                }
                else{
                    b = b + HowMuch;
                }
                pix[index] = 0xff000000 | (flatten(r) << 16) | (flatten(g) << 8) | flatten(b);
            }
        }

            bit.setPixels(pix, 0, w, 0, 0, w, h);




            doNotRepeat = 0;
        return bit;

    }
                */
        /**
         * Casts the image to greyscale.
         */
        static Bitmap toGrayscale(Bitmap bitmap) {


            setH(bitmap.getHeight());
            setW(bitmap.getWidth());
            pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);
            Bitmap bit = Bitmap.createBitmap(bitmap);
            int index, r,g,b,avgRGB;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    index = y * w + x;
                    r = (pix[index] >> 16) & 0xff;
                    g = (pix[index] >> 8) & 0xff;
                    b = pix[index] & 0xff;
                    avgRGB = (r + g + b) / 3;
                    pix[index] = 0xff000000 | (flatten(avgRGB) << 16) | (flatten(avgRGB) << 8) | flatten(avgRGB);
                }
            }

            bit.setPixels(pix, 0, w, 0, 0, w, h);

            TakePhotoActivity.myImage.setFilter(1);
            return bit;
        }

    /**
     * Casts the image to sepia colors.
     */
    static Bitmap toSepia(Bitmap bitmap) {

        setH(bitmap.getHeight());
        setW(bitmap.getWidth());
        pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        Bitmap bit = Bitmap.createBitmap(bitmap);
        int index, r,g,b,redSepia, greenSepia, blueSepia;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                index = y * w + x;
                r = (pix[index] >> 16) & 0xff;
                g = (pix[index] >> 8) & 0xff;
                b = pix[index] & 0xff;
                redSepia = ((Double)(0.393*r + 0.769*g + 0.189*b)).intValue();
                greenSepia = ((Double)(0.349*r + 0.686*g + 0.168*b)).intValue();
                blueSepia = ((Double)(0.272*r + 0.534*g + 0.131*b)).intValue();
                pix[index] = 0xff000000 | (flatten(redSepia) << 16) | (flatten(greenSepia) << 8) | flatten(blueSepia);
            }
        }

        bit.setPixels(pix, 0, w, 0, 0, w, h);

        TakePhotoActivity.myImage.setFilter(2);

        return bit;
    }

        /**
         * Inverts all the colors in the image.
         */

        static Bitmap invert(Bitmap bitmap) {

            setH(bitmap.getHeight());
            setW(bitmap.getWidth());
            pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);
            Bitmap bit = Bitmap.createBitmap(bitmap);
            int index, r,g,b,red,green,blue;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    index = y * w + x;
                    r = (pix[index] >> 16) & 0xff;
                    g = (pix[index] >> 8) & 0xff;
                    b = pix[index] & 0xff;
                    red = 255 - r;
                    green = 255-g;
                    blue = 255-b;
                    pix[index] = 0xff000000 | (flatten(red) << 16) | (flatten(green) << 8) | flatten(blue);
                }
            }

            bit.setPixels(pix, 0, w, 0, 0, w, h);


            TakePhotoActivity.myImage.setFilter(3);

            return bit;

        }
}
