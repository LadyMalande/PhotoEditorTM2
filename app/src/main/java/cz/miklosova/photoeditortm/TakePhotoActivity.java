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

    public static MyImage myImage;


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
    public static ImageView imageView;
    public static TextView brightnessNumber;
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
        brightnessNumber = findViewById(cz.miklosova.photoeditortm.R.id.textView_brightnessNumber);
        contrastNumber = findViewById(cz.miklosova.photoeditortm.R.id.textView_contrastNumber);

        seekBar_contrast.setOnSeekBarChangeListener(new ContrastListener());
    }
/*
    @Override
    protected void onSaveInstanceState(Bundle thisBundle) {
        super.onSaveInstanceState(thisBundle);
        if (myImage != null) {
            ProxyBitmap pb = new ProxyBitmap(myImage.getFullSizeBitmap());
            ProxyBitmap pbWithFilter = new ProxyBitmap(myImage.getThumbnailWithSelectedFilter());
            thisBundle.putSerializable("fullSizeBitmap", pb);
            thisBundle.putSerializable("thumbnailWithSelectedFilter", pbWithFilter);
            thisBundle.putInt("brightness", myImage.getBrightness());
            thisBundle.putFloat("contrast", myImage.getContrast());
            thisBundle.putInt("filter", myImage.getFilter());
        }
    }

*/

    public static int getSizeWidthImageView() {
        return imageView.getWidth();
    }

    public static int getSizeHeightImageView() {
        return imageView.getHeight();
    }

    private String createImageName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return "JPEG_" + timeStamp;
    }

    @TargetApi(26)
    private String createImageNameNewerSDK(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.US);
        LocalDateTime time = LocalDateTime.now();
        String f = formatter.format(time);
        return "JPEG_NEW" + f;
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // image successfully taken by the camera
            try {
                enableAllEditorButtons(true, 0);

                ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
                File file = wrapper.getDir("PhotosTakenWithPETM", MODE_PRIVATE);
                // Create a file to save the image
                file = new File(file, FILE_TITLE);
                OutputStream stream;
                stream = new FileOutputStream(file);
                MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
                // Parse the gallery image url to uri
                new MyScanner(this, file);
                // Runtime.getRuntime().exec("chmod 777 " + file.getAbsolutePath());


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
            enableAllEditorButtons(false, 4);

        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
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

    private void preparePreviews() {
        int widthOfPreviewButton = filterButton_noFilter.getWidth();
        int heightOfPreviewButton = filterButton_noFilter.getHeight();



        Bitmap previewClear;
        previewClear = Bitmap.createScaledBitmap(myImage.getThumbnailBackup(), widthOfPreviewButton, heightOfPreviewButton, true);



        new ImageFilterTask().execute(previewClear);
        /*
        filterButton_grey.setImageBitmap(Filters.toGrayscale(previewClear));
        filterButton_sepia.setImageBitmap(Filters.toSepia(previewClear));
        filterButton_invert.setImageBitmap(Filters.invert(previewClear));
        filterButton_polaroid.setImageBitmap(Filters.polaroid(previewClear));
        */
        myImage.setFilter(0);
    }

    class ImageFilterTask extends AsyncTask<Bitmap, Void, Bitmap[]> {
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

    public void DoGrey(View view) {
        if (view.getId() == cz.miklosova.photoeditortm.R.id.filter_Button_grey) {
            myImage.setThumbnailWithSelectedFilter(Filters.toGrayscale(myImage.getThumbnailBackup()));
            myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                    myImage.getContrast(), myImage.getBrightness()));
            imageView.setImageBitmap(myImage.getThumbnail());
            imageView.invalidate();
        } else {
            myImage.setFullSizeBitmap(Filters.toGrayscale(myImage.getFullSizeBitmap()));
        }
    }

    public void DoSepia(View view) {
        if (view.getId() == cz.miklosova.photoeditortm.R.id.filter_Button_sepia) {
            myImage.setThumbnailWithSelectedFilter(Filters.toSepia(myImage.getThumbnailBackup()));
            myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                    myImage.getContrast(), myImage.getBrightness()));
            imageView.setImageBitmap(myImage.getThumbnail());
            imageView.invalidate();
        } else {
            myImage.setFullSizeBitmap(Filters.toSepia(myImage.getFullSizeBitmap()));
        }
    }

    public void DoInvert(View view) {
        if (view.getId() == cz.miklosova.photoeditortm.R.id.filter_Button_invert) {
            myImage.setThumbnailWithSelectedFilter(Filters.invert(myImage.getThumbnailBackup()));
            myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                    myImage.getContrast(), myImage.getBrightness()));
            imageView.setImageBitmap(myImage.getThumbnail());
            imageView.invalidate();
        } else {
            myImage.setFullSizeBitmap(Filters.invert(myImage.getFullSizeBitmap()));
        }
    }

    public void DoPolaroid(View view) {
        if (view.getId() == cz.miklosova.photoeditortm.R.id.filter_Button_polaroid) {
            myImage.setThumbnailWithSelectedFilter(Filters.polaroid(myImage.getThumbnailBackup()));
            myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                    myImage.getContrast(), myImage.getBrightness()));
            imageView.setImageBitmap(myImage.getThumbnail());
            imageView.invalidate();
        } else {
            myImage.setFullSizeBitmap(Filters.polaroid(myImage.getFullSizeBitmap()));
        }
    }

    public void DoNoFilter(View view) {
        myImage.setThumbnailWithSelectedFilter(myImage.getThumbnailBackup());
        myImage.setThumbnail(Filters.changeBitmapContrastBrightness(myImage.getThumbnailWithSelectedFilter(),
                myImage.getContrast(), myImage.getBrightness()));
        imageView.setImageBitmap(myImage.getThumbnail());
        imageView.invalidate();
        myImage.setFilter(0);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

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


    public void saveImage(View view){

        imageButton_save.setClickable(false);

        modifyFullSizeBitmap(view);
        try {

        File file;
        // Create a file to save the image



            if (android.os.Build.VERSION.SDK_INT >= 26) {
                file = File.createTempFile(createImageName(), ".jpg", this.getFilesDir());

            } else {
                File dir = getPublicAlbumStorageDir("PhotoEditorTM");
                file = File.createTempFile(createImageName(), ".jpg", dir);
            }

            file.mkdir();
            FileOutputStream stream;
            stream = new FileOutputStream(file);
            myImage.getFullSizeBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();

            // Parse the gallery image url to uri
            Uri savedImageURI = Uri.parse(file.getAbsolutePath());

            MediaScannerConnection.scanFile(TakePhotoActivity.this,
                    new String[]{file.getAbsolutePath()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {

                        }
                    });
            Toast.makeText(TakePhotoActivity.this, "scan complete", Toast.LENGTH_SHORT).show();

//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, savedImageURI));
             //       MyScanner ms = new MyScanner(TakePhotoActivity.this, file);
             //       ms.scan();
             //       Runtime.getRuntime().exec("chmod 777 " + file.getAbsolutePath());

                    // Display saved image uri to TextView
                    //Toast.makeText(this, savedImageURI.toString(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "FUCKED UP NULLPOINTEREX", Toast.LENGTH_LONG).show();
        }
        imageButton_save.setClickable(true);
    }

    private void modifyFullSizeBitmap(View view) {

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
        myImage.setFullSizeBitmap((Filters.changeBitmapContrastBrightness(myImage.getFullSizeBitmap(),
                myImage.getContrast(), myImage.getBrightness())));

    }

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

    private void enableAllEditorButtons(boolean b, int visibility ){
        imageButton_filters.setEnabled(b);
        imageButton_contrast.setEnabled(b);
        imageButton_save.setEnabled(b);
        LLfilters.setVisibility(visibility);
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}