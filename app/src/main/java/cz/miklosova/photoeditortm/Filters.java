package cz.miklosova.photoeditortm;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;


class Filters {

        /**
         * This method is used to prevent unwanted numbers in RGB color scheme.
         */
        private static int flatten(int i) {
            if (i > 255) {
                return 255;
            } else if (i < 0) {
                return 0;
            } else {
                return i;
            }
        }

    /**
     * This method creates brightness/contrast ColorMatrix.
      * @param contrast What contrast value should be applied.
     * @param brightness What brightness value should be applied.
     * @return Returns the complete matrix.
     */
    private static ColorMatrix createColorMatrix(float contrast, float brightness) {
            return new ColorMatrix(new float[]{
                  contrast, 0, 0, 0, brightness,
                  0, contrast, 0, 0, brightness,
                  0, 0, contrast, 0, brightness,
                  0, 0, 0, 1, 0
          });
    }

    /**
     * This method handles the change of brightness and contrast in a bitmap.
     * @param bmp What bitmap should be affected.
     * @param contrast What contrast value should be applied.
     * @param brightness What brightness value should be applied.
     * @return Returns the filtered Bitmap.
     */
    static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(createColorMatrix(contrast, brightness)));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    /**
     * This method applies polaroid filter to Bitmap.
     * @param bmp What bitmap should be affected.
     * @return Returns the filtered Bitmap.
     */
    static Bitmap polaroid(Bitmap bmp) {
        final ColorMatrix POLAROID_MATRIX = new ColorMatrix(new float[]
                {
                        1.438f, -0.062f, -0.062f,0, 0,
                        -0.122f, 1.378f, -0.122f, 0, 0,
                        -0.016f, -0.016f, 1.483f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(POLAROID_MATRIX));
        canvas.drawBitmap(bmp, 0, 0, paint);
        TakePhotoActivity.myImage.setFilter(4);
        return ret;
    }


    /**
     * Casts the image to greyscale.
     * @param bitmap What bitmap should be affected.
     * @return Returns the filtered Bitmap.
     */
    static Bitmap toGrayscale(Bitmap bitmap) {
    int w = bitmap.getWidth();
    int h = bitmap.getHeight();
    int[] pix = new int[w * h];
    bitmap.getPixels(pix, 0, w, 0, 0, w, h);
    Bitmap bit = Bitmap.createBitmap(bitmap);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index, r,g,b,avgRGB;
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
     * @param bitmap What bitmap should be affected.
     * @return Returns the filtered Bitmap.
     */
    static Bitmap toSepia(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        Bitmap bit = Bitmap.createBitmap(bitmap);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index, r,g,b,redSepia, greenSepia, blueSepia;
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
     * Inverts all the colors in the Bitmap.
     * @param bitmap What bitmap should be affected.
     * @return Returns the filtered Bitmap.
     */
    static Bitmap invert(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        Bitmap bit = Bitmap.createBitmap(bitmap);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index, r,g,b,red,green,blue;
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
