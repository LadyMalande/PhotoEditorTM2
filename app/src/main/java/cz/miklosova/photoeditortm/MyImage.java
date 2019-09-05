package cz.miklosova.photoeditortm;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Class MyImage contains copies of original image selected or captured by user.
 * It also contains variables to store vital data for saving the exact image as is shown in thumbnail.
 */
class MyImage {

    /**
     * Variable that holds the original Bitmap in reasonable size.
     */
    private Bitmap fullSizeBitmap;

    /**
     * Variable that holds the updated version of the picture shown in ImageView. Filters, brightness and contrast is applied.
     */
    private Bitmap thumbnail;

    /**
     * Variable that holds the original Bitmap in reduced size with no filters or contrast/brightness apllied.
     */
    private Bitmap thumbnailBackup;

    /**
     * Variable that holds reduced Bitmap with filter applied so changing brightness/contrast is faster.
     */
    private Bitmap thumbnailWithSelectedFilter;

    /**
     * Variable that holds an integer number from 0 to ... that indicates which filter is used.
     */
    private int filter;

    /**
     * Variable that holds an integer indicating what value of brightness is applied to thumbnail.
     */
    private int brightness;

    /**
     * Variable that holds a float indicating what value of contrast is applied to thumbnail.
     */
    private float contrast;

    /**
     *
     * @return Returns original image full size.
     */
    Bitmap getFullSizeBitmap(){
        return fullSizeBitmap;
    }

    public void setFullSizeBitmap(Bitmap fullSizeBitmap) {
        this.fullSizeBitmap = fullSizeBitmap;
    }

    /**
     *
     * @return Returns thumbnail = image with filter, brightness and contrast applied.
     */
    Bitmap getThumbnail(){
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getThumbnailBackup() {
        return thumbnailBackup;
    }

    public void setThumbnailBackup(Bitmap thumbnailBackup) {
        this.thumbnailBackup = thumbnailBackup;
    }

    public Bitmap getThumbnailWithSelectedFilter() {
        return thumbnailWithSelectedFilter;
    }

    public void setThumbnailWithSelectedFilter(Bitmap thumbnailWithSelectedFilter) {
        this.thumbnailWithSelectedFilter = thumbnailWithSelectedFilter;
    }

    public int getFilter() {
        return filter;
    }

    /**
     *
     * @param i Sets the integer of filter id.
     */
    void setFilter(int i){
        this.filter = i;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    /**
     * Constructor of MyImage class.
     * @param bitmap is an original size Bitmap created by Camera of selected from gallery
     */
    MyImage(Bitmap bitmap) {
        // Original bitmap is stored in variable fullSizeBitmap
        this.fullSizeBitmap = bitmap;
        int maxHeight = TakePhotoActivity.getSizeHeightImageView();
        int maxWidth = TakePhotoActivity.getSizeWidthImageView();
        // Scale is computed to set the number to size changing matrix.
        float scale = Math.min(((float) maxHeight / bitmap.getWidth()), ((float) maxWidth / bitmap.getHeight()));

        // Matrix for reducing size is created, scale indicating how to scale the Bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // 3 copies of reduced size original Bitmap are created and stored to given variables
        this.thumbnail = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.thumbnailWithSelectedFilter = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.thumbnailBackup = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

         // filter starts as 0 = no filter, brightness as 0 = brightness not changed, contrast as 1 = contrast not changed
        this.filter = 0;
        this.brightness = 0;
        this.contrast = 1;

    }


}
