package cz.miklosova.photoeditortm;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity provides most of the functionality of the app.
 * It takes the layout with filter buttons and contrast sliders and shows thumbnail with changes to user.
 */
public class TakePhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_GALLERY = 20;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int FILTER_NONE = 0;
    static final int FILTER_GREYSCALE = 1;
    static final int FILTER_SEPIA = 2;
    static final int FILTER_INVERT = 3;
    static final int FILTER_POLAROID = 4;
    private static final String TAG = TakePhotoActivity.class.getName();
    private static final String PHOTO_TITLE = "MyCapturedPhoto";
    private static final String FILE_TITLE = "MyFile.jpg";
    public static int imageViewWidth;
    public static int imageViewHeight;
    private Uri imageUri;

    /**
     * An object which holds all the needed data to manipulate the Bitmap.
     */
    public static MyImage myImage;


    // assigning components which will be needed by this class
    @BindView(R.id.seekBar_brightness) SeekBar seekBar_brightness;
    @BindView(R.id.seekBar_contrast) SeekBar seekBar_contrast;
    @BindView(R.id.filter_button_noFilter) ImageButton filterButton_noFilter;
    @BindView(R.id.filter_Button_grey) ImageButton filterButton_grey;
    @BindView(R.id.filter_Button_sepia) ImageButton filterButton_sepia;
    @BindView(R.id.filter_Button_invert) ImageButton filterButton_invert;
    @BindView(R.id.filter_Button_polaroid) ImageButton filterButton_polaroid;
    @BindView(R.id.layoutSliders) LinearLayout slidersLayout;
    @BindView(R.id.imageButton_filters) ImageButton imageButton_filters;
    @BindView(R.id.imageButton_contrast) ImageButton imageButton_contrast;
    @BindView(R.id.imageButton_Save) ImageButton imageButton_save;
    @BindView(R.id.linearLayout_Filters) LinearLayout LLfilters;

    /**
     * The main ImageView of this activity. It shows the thumbnail with applied filters and contrast changes.
     */
    public static ImageView imageView;

    /**
     * This TextView shows the parameter of brightness.
     */
    public static TextView brightnessNumber;

    /**
     * This TextView shows the parameter of contrast.
     */
    public static TextView contrastNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);


        setContentView(R.layout.editor_activity);
        ButterKnife.bind(this);
        imageView = findViewById(R.id.imageView);

        imageViewHeight = imageView.getHeight();
        imageViewWidth = imageView.getWidth();
        if(myImage != null){
            int filterSave = myImage.getFilter();
            imageView.setImageBitmap(myImage.getThumbnail());
            Thread waitABit = new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                        Thread.sleep(100);
                        preparePreviews();
                    } catch (Exception e){
                        Log.e(TAG, "bad thread sleep before preparePreviews");
                    }
                }
            });
            waitABit.start();
            myImage.setFilter(filterSave);
            imageView.invalidate();
            imageView.postInvalidate();
        }

        seekBar_brightness.setOnSeekBarChangeListener(new BrightnessListener());
        seekBar_contrast.setOnSeekBarChangeListener(new ContrastListener());
        brightnessNumber = findViewById(cz.miklosova.photoeditortm.R.id.textView_brightnessNumber);
        contrastNumber = findViewById(cz.miklosova.photoeditortm.R.id.textView_contrastNumber);
    }

    /**
     * Gives the width of imageView.
     * @return Returns the width of imageView.
     */
    public static int getSizeWidthImageView() {
        return imageView.getWidth();
    }

    /**
     * Gives the height of imageView.
     * @return Returns the height of imageView.
     */
    public static int getSizeHeightImageView() {
        return imageView.getHeight();
    }

    /**
     * Creates unique name for the file to be saved.
     * @return String of created name.
     */
    private String createImageName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return "JPEG_" + timeStamp;
    }

    /**
     * Creates unique string value for the file name. Only for newer APIs because of DateTimeFormatter.
     * @return String of created name.
     */
    @TargetApi(26)
    private String createImageNameNewerSDK(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.US);
        LocalDateTime time = LocalDateTime.now();
        String f = formatter.format(time);
        return "JPEG_NEW" + f;
    }

    /**
     * Starts the intent to take an image with camera.
     * @param view Which View started this.
     */
    public void prepareForPhoto(View view) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, PHOTO_TITLE);
        values.put(MediaStore.Images.Media.DESCRIPTION, this.getString(R.string.photo_taken_on) + System.currentTimeMillis()); //
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Toast.makeText(this, imageUri.getPath(), Toast.LENGTH_LONG).show();
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);

    }

    /**
     * Starts the intent to browse the gallery.
     * @param view Which View started this.
     */
    public void prepareForGallery(View view) {
        //invoke the image gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);

        //where we find the data
        File galleryDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String galleryPath = galleryDirectory.getPath();
        //now get the URI representation of the path
        Uri galleryURI = Uri.parse(galleryPath);

        // set where we want to get pictures from and what types
        galleryIntent.setDataAndType(galleryURI, "image/*");

        //invoke the activity and get the result
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    /**
     * This method is called to avoid graphical confusion wheteher the brightness/contrast filters
     * were already applied after loading new image.
     */
    private void resetSeekBars(){
        seekBar_brightness.setProgress(255);
        seekBar_contrast.setProgress(100);
        brightnessNumber.setText(this.getString(R.string.zero));
        contrastNumber.setText(this.getString(R.string.one));
    }

    /**
     * This method handles the results of Intents we start with gallery or camera.
     * @param requestCode What intent was supposed to happen.
     * @param resultCode If the intent ended successfully or not.
     * @param data What Intent is this about.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // image successfully taken by the camera
            try {
                resetSeekBars();
                enableAllEditorButtons(true, 0);

                ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
                File file = wrapper.getDir(this.getString(R.string.image_taken_with_petm), MODE_PRIVATE);
                // Create a file to save the image
                file = new File(file, FILE_TITLE);
                OutputStream stream;
                stream = new FileOutputStream(file);
                MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();

                new MyScanner(this, file);

                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

                BitmapFactory.Options opts2 = new BitmapFactory.Options();
                opts2.inJustDecodeBounds = false;
                opts2.inMutable = true;
                opts2.inSampleSize = calculateSampleSize(opts.outWidth, opts.outHeight);

                //we have the input stream, we get the bitmap from it
                myImage = new MyImage(BitmapFactory.decodeFile(file.getAbsolutePath(), opts2));

                preparePreviews();
                file.delete();
                imageView.setImageBitmap(myImage.getThumbnail());
            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFoundException in onActivityResult REQUEST_IMAGE_CAPTURE");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "IOException in onActivityResult REQUEST_IMAGE_CAPTURE");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException in onActivityResult REQUEST_IMAGE_CAPTURE");
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            // Don't enable buttons, something went wrong.
            enableAllEditorButtons(false, 4);

        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            resetSeekBars();
            // Enable the buttons, we will get the Bitmap
            enableAllEditorButtons(true, 0);
            //image chosen successfully from gallery
            Uri imageURI = data.getData();

            //Stream for reading the image from SD card
            InputStream inputStream, inputStream2;

            //get inputStream from declared URI of the image
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;

                inputStream = getContentResolver().openInputStream(imageURI);
                inputStream2 = getContentResolver().openInputStream(imageURI);
                BitmapFactory.decodeStream(inputStream, null, opts);

                BitmapFactory.Options opts2 = new BitmapFactory.Options();
                opts2.inJustDecodeBounds = false;
                opts2.inMutable = true;
                opts2.inSampleSize = calculateSampleSize(opts.outWidth, opts.outHeight);
                //we have the input stream, we get the bitmap from it
                myImage = new MyImage(BitmapFactory.decodeStream(inputStream2, null, opts2));

                preparePreviews();
                imageView.setImageBitmap(myImage.getThumbnail());


            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFoundException in onActivityResult REQUEST_IMAGE_GALLERY");
                e.printStackTrace();
                Toast.makeText(this, "Unable to read image", Toast.LENGTH_LONG).show();

            }
            catch(NullPointerException nulle){
                Log.e(TAG, "NullPointerException in onActivityResult REQUEST_IMAGE_GALLERY");
                nulle.printStackTrace();
                Toast.makeText(this, "Null input stream", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * This method computes the preview thumnails which are to be set on the ImageButtons of filters.
     */
    private void preparePreviews() {
        int widthOfPreviewButton = filterButton_noFilter.getWidth();
        int heightOfPreviewButton = filterButton_noFilter.getHeight();

        Bitmap previewClear;
        previewClear = Bitmap.createScaledBitmap(myImage.getThumbnailBackup(), widthOfPreviewButton, heightOfPreviewButton, true);

        new ImageFilterTask().execute(previewClear);

        myImage.setFilter(0);
    }

    /**
     * This class helps to load preview bitmaps on buttons more smoothly.
     */
    class ImageFilterTask extends AsyncTask<Bitmap, Void, Bitmap[]> {

        /**
         * This method is started with ImageFilterTask.execute and is computing bitmap values for ImageButtons
         * @param bitmaps Actually we want to send just one bitmap and that is previewClear
         * @return We return an array with all the computed previews.
         */
        @Override
        protected Bitmap[] doInBackground(Bitmap... bitmaps) {
            Bitmap[] bitmapArray = new Bitmap[5];
            bitmapArray[0] = Filters.toGrayscale(bitmaps[0]);
            bitmapArray[1] = Filters.toSepia(bitmaps[0]);
            bitmapArray[2] = Filters.invert(bitmaps[0]);
            bitmapArray[3] = Filters.polaroid(bitmaps[0]);
            bitmapArray[4] = bitmaps[0];
            return bitmapArray;
        }

        /**
         * We set the bitmaps on their ImageButtons if everything was ok.
         * @param bitmap We take the result from doInBackground.
         */
        @Override
        protected void onPostExecute(Bitmap[] bitmap) {
            if (isCancelled()) {
                bitmap = null;
            } else {
                filterButton_grey.setImageBitmap(bitmap[0]);
                filterButton_sepia.setImageBitmap(bitmap[1]);
                filterButton_invert.setImageBitmap(bitmap[2]);
                filterButton_polaroid.setImageBitmap(bitmap[3]);
                filterButton_noFilter.setImageBitmap(bitmap[4]);
            }
        }
    }

    /**
     * This method calculates the scale which should be used to make a reasonable-sized bitmap.
     * @param w Width of the original bitmap.
     * @param h Height of the original bitamp.
     * @return Returns the int value of winning scale number.
     */
    private int calculateSampleSize(int w, int h) {
        int scaleOfWidth;
        scaleOfWidth = 2;
        int rest;
        rest = w / scaleOfWidth;
        if (w <= 1000 && h <= 1000) {
            return 1;
        }
        while (rest > 1000) {
            rest = w / scaleOfWidth;
            scaleOfWidth++;
        }

        int scaleOfHeight;
        scaleOfHeight = 2;
        rest = h / scaleOfHeight;
        while (rest > 1000) {
            rest = h / scaleOfHeight;
            scaleOfHeight++;
        }
        // we take the larger number for scaling
        int biggerScale;
        if (scaleOfWidth > scaleOfHeight) {
            biggerScale = scaleOfWidth;
        } else {
            biggerScale = scaleOfHeight;
        }
        return biggerScale;
    }

    /**
     * Method for greyscale button to start thumbnail change and myImage variables change.
     * @param view which view sent this method
     */
    public void DoGrey(View view) {
        // if it was started by preview filter button, just change the thumbnail and variables in myImage
        if (view.getId() == cz.miklosova.photoeditortm.R.id.filter_Button_grey) {
            myImage.setThumbnailWithSelectedFilter(Filters.toGrayscale(myImage.getThumbnailBackup()));
            myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                    myImage.getContrast(), myImage.getBrightness()));
            imageView.setImageBitmap(myImage.getThumbnail());
            imageView.invalidate();
        } else {
            // if it was sent by anything other, it must have been the save button, so modify the fullsizebitmap
            myImage.setFullSizeBitmap(Filters.toGrayscale(myImage.getFullSizeBitmap()));
        }
    }

    /**
     * Method for sepia button to start thumbnail change and myImage variables change.
     * @param view which view sent this method
     */
    public void DoSepia(View view) {
        // if it was started by preview filter button, just change the thumbnail and variables in myImage
        if (view.getId() == cz.miklosova.photoeditortm.R.id.filter_Button_sepia) {
            myImage.setThumbnailWithSelectedFilter(Filters.toSepia(myImage.getThumbnailBackup()));
            myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                    myImage.getContrast(), myImage.getBrightness()));
            imageView.setImageBitmap(myImage.getThumbnail());
            imageView.invalidate();
        } else {
            // if it was sent by anything other, it must have been the save button, so modify the fullsizebitmap
            myImage.setFullSizeBitmap(Filters.toSepia(myImage.getFullSizeBitmap()));
        }
    }

    /**
     * Method for invert button to start thumbnail change and myImage variables change.
     * @param view which view sent this method
     */
    public void DoInvert(View view) {
        // if it was started by preview filter button, just change the thumbnail and variables in myImage
        if (view.getId() == cz.miklosova.photoeditortm.R.id.filter_Button_invert) {
            myImage.setThumbnailWithSelectedFilter(Filters.invert(myImage.getThumbnailBackup()));
            myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                    myImage.getContrast(), myImage.getBrightness()));
            imageView.setImageBitmap(myImage.getThumbnail());
            imageView.invalidate();
        } else {
            // if it was sent by anything other, it must have been the save button, so modify the fullsizebitmap
            myImage.setFullSizeBitmap(Filters.invert(myImage.getFullSizeBitmap()));
        }
    }

    /**
     * Method for polaroid button to start thumbnail change and myImage variables change.
     * @param view which view sent this method
     */
    public void DoPolaroid(View view) {
        // if it was started by preview filter button, just change the thumbnail and variables in myImage
        if (view.getId() == cz.miklosova.photoeditortm.R.id.filter_Button_polaroid) {
            myImage.setThumbnailWithSelectedFilter(Filters.polaroid(myImage.getThumbnailBackup()));
            myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                    myImage.getContrast(), myImage.getBrightness()));
            imageView.setImageBitmap(myImage.getThumbnail());
            imageView.invalidate();
        } else {
            // if it was sent by anything other, it must have been the save button, so modify the fullsizebitmap
            myImage.setFullSizeBitmap(Filters.polaroid(myImage.getFullSizeBitmap()));
        }
    }

    /**
     * Method for no filter button to start thumbnail change and myImage variables change.
     * @param view which view sent this method
     */
    public void DoNoFilter(View view) {
        myImage.setThumbnailWithSelectedFilter(myImage.getThumbnailBackup());
        myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                myImage.getContrast(), myImage.getBrightness()));
        imageView.setImageBitmap(myImage.getThumbnail());
        imageView.invalidate();
        myImage.setFilter(0);
    }

    /**
     * Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * This method creates or just returns custom directory for photos. Works on my device of version 19, but didnt work in emulator version 29
     * @param albumName custom album name to be used which the user will see in gallery folders
     * @return returns the directory as File variable
     */
    public File getPublicAlbumStorageDir(String albumName) {
        if (isExternalStorageWritable()) {
            // Get the directory for the user's public pictures directory.
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
            return file;
        }
        else {
            return null;
        }
    }


    /**
     * This method does the job of saving the original-sized bitmap with all the filters and contrast to file.
     * @param view which view started this method
     */
    public void saveImage(View view){
        // don't allow another saving in the meantime
        imageButton_save.setClickable(false);
        // modify the original-sized bitmap
        modifyFullSizeBitmap(view);
        try {
        File file;
        // Create a file to save the image. If the version of SDK is newer, we can use LocalDateTime
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                file = File.createTempFile(createImageNameNewerSDK(), ".jpg", this.getFilesDir());

            } else {
                File dir = getPublicAlbumStorageDir("PhotoEditorTM");
                file = File.createTempFile(createImageName(), ".jpg", dir);
            }
            // make sure the file exists
            file.mkdir();
            FileOutputStream stream;
            stream = new FileOutputStream(file);
            //compress the bitmap to the stream, then flush it to save it to the file
            myImage.getFullSizeBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();

            // Parse the gallery image url to uri
            Uri savedImageURI = Uri.parse(file.getAbsolutePath());

            // THIS PART DOESN'T WORK PROPERLY - it should scan the file and make a preview of it to gallery/photo directory
            MediaScannerConnection.scanFile(TakePhotoActivity.this,
                    new String[]{file.getAbsolutePath()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            Toast.makeText(TakePhotoActivity.this, "Scan completed.", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Tell to the user where his image is
            Toast.makeText(this, savedImageURI.toString(), Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "IOException in saveImage method");
            Toast.makeText(this, "ioex", Toast.LENGTH_LONG).show();
        }
        catch(NullPointerException e){
            Log.e(TAG, "NullPointerException in saveImage method");
            Toast.makeText(this, "NullPointerException during save method", Toast.LENGTH_LONG).show();
        }
        // Make possible to save another image
        imageButton_save.setClickable(true);
    }

    /**
     * This method does filtering and brightnes/contrast changes on the fullsizebitmap.
     * @param view which View started this method
     */
    private void modifyFullSizeBitmap(View view) {
        // we check filter value to know which filter to apply
        switch(myImage.getFilter()) {
            case FILTER_NONE: {}
            break;
            case FILTER_GREYSCALE: DoGrey(view);
                break;
            case FILTER_SEPIA: DoSepia(view);
                break;
            case FILTER_INVERT: DoInvert(view);
                break;
            case FILTER_POLAROID: DoPolaroid(view);
                break;
        }
        // afterwards we apply contrast and brightness
        myImage.setFullSizeBitmap((Filters.changeBitmapContrastBrightness(myImage.getFullSizeBitmap(),
                myImage.getContrast(), myImage.getBrightness())));

    }

    /**
     * This method functions as a View switch. It makes visible the contrast/brightness SeekBars
     * and makes invisible the filters previews.
     * @param view which View started this method
     */
    public void MakeVisibleContrast(View view) {
        slidersLayout.setVisibility(View.VISIBLE);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            HorizontalScrollView scrollView = findViewById(R.id.scroll_view);
            scrollView.setVisibility(View.INVISIBLE);
        } else {
            ScrollView scrollView = findViewById(R.id.scroll_view_land);
            scrollView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Functions as a View switch = it brings forward the filter previews and makes invisible the contrast
     * and brightness sliders.
     * @param view which View started this method
     */
    public void MakeVisibleFilters(View view){
        slidersLayout.setVisibility(View.INVISIBLE);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            HorizontalScrollView scrollView = findViewById(cz.miklosova.photoeditortm.R.id.scroll_view);
            scrollView.setVisibility(View.VISIBLE);
        } else {
            ScrollView scrollView = findViewById(cz.miklosova.photoeditortm.R.id.scroll_view_land);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method enables the buttons that modify bitmap. It is started after any image has been loaded (from camera of gallery)
     * @param b If b is true = it enables the buttons. If b is false, it doesn't enable the buttons.
     * @param visibility Sets the visibility value of LinearLayout of filters buttons
     */
    private void enableAllEditorButtons(boolean b, int visibility ){
        imageButton_filters.setEnabled(b);
        imageButton_contrast.setEnabled(b);
        imageButton_save.setEnabled(b);
        LLfilters.setVisibility(visibility);
    }
}