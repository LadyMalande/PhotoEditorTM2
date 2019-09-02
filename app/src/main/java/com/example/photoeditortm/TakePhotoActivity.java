package com.example.photoeditortm;

import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_GALLERY = 20;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    public static int imageViewWidth, imageViewHeight;
    private Uri imageUri;
    static public ImageView imageView;
    public static MyImage myImage;
    SeekBar seekBar_brightness, seekBar_contrast;
    public static TextView noname,grey;
    public static TextView brightnessNumber,contrastNumber;
    private int widthOfPreviewButton;
    public static Uri CONTENT_URI;
    private int heightOfPreviewButton;
    private Bitmap previewClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        imageView = findViewById(R.id.imageView);
        imageViewHeight = imageView.getHeight();
        imageViewWidth = imageView.getWidth();
        CONTENT_URI = Uri.parse("content://com.example.photoeditortm/");
        seekBar_brightness= findViewById(R.id.seekBar_brightness);
        seekBar_brightness.setOnSeekBarChangeListener(new BrightnessListener());
        brightnessNumber= findViewById(R.id.textView_brightnessNumber);
        contrastNumber= findViewById(R.id.textView_contrastNumber);
        noname = findViewById(R.id.textView_noFilter);
        grey = findViewById(R.id.textView_grey);
        seekBar_contrast=findViewById(R.id.seekBar_contrast);
        seekBar_contrast.setOnSeekBarChangeListener(new ContrastListener());
        ImageButton filterButton = findViewById(R.id.filter_button_noFilter);
        widthOfPreviewButton = filterButton.getWidth();
        heightOfPreviewButton = filterButton.getHeight();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){

        final ImageView imageView = findViewById(R.id.imageView);
        if(myImage != null){
            imageView.setImageBitmap(myImage.getThumbnail());
        }
    }

    public static int getSizeWidthImageView(){
        return imageView.getWidth();
    }

    public static int getSizeHeightImageView(){
        return imageView.getHeight();
    }

    private String createImageName(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }

    public void prepareForPhoto(View view){
    ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"MyPicture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Photo taken on "+System.currentTimeMillis());
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        Toast.makeText(this, imageUri.getPath(), Toast.LENGTH_LONG).show();
    startActivityForResult(intent, REQUEST_TAKE_PHOTO);

    }

    public void prepareForGallery(View view){
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
               enableAllEditorButtons(true, 0 );
               //myImage = new MyImage(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));

                   ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
                   File file = wrapper.getDir("Pictures", MODE_PRIVATE);
                   // Create a file to save the image
                   file = new File(file,  "tralalalal.jpg");
                   OutputStream stream = null;
                   stream = new FileOutputStream(file);
                   MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri).compress(Bitmap.CompressFormat.JPEG,100,stream);
                   stream.flush();
                   stream.close();
                   // Parse the gallery image url to uri
                   Uri savedImageURI = Uri.parse(file.getAbsolutePath());
                   //new MyScanner(this, file);
                   Runtime.getRuntime().exec("chmod 777 " + file.getAbsolutePath());



                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

                    BitmapFactory.Options opts2 = new BitmapFactory.Options();
                    opts2.inJustDecodeBounds = false;
                    opts2.inMutable = true;
                    opts2.inSampleSize = calculateSampleSize(opts.outWidth,opts.outHeight);
               //we have the input stream, we get the bitmap from it
               myImage = new MyImage(BitmapFactory.decodeFile(file.getAbsolutePath(), opts2));
                preparePreviews();
               file.delete();
               //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
               imageView.setImageBitmap(myImage.thumbnail);
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
           catch(NullPointerException e){
               Toast.makeText(this,"null pointer ex here", Toast.LENGTH_LONG).show();
           }
       }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED){
           enableAllEditorButtons(false, 4);

       }else if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK){
           enableAllEditorButtons(true, 0 );
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
               BitmapFactory.decodeStream(inputStream,null, opts);

               BitmapFactory.Options opts2 = new BitmapFactory.Options();
               opts2.inJustDecodeBounds = false;
               opts2.inMutable = true;
               opts2.inSampleSize = calculateSampleSize(opts.outWidth,opts.outHeight);
               //we have the input stream, we get the bitmap from it
               myImage = new MyImage(BitmapFactory.decodeStream(inputStream2,null, opts2));
               //bitmap = BitmapFactory.decodeStream(inputStream);


               preparePreviews();
               imageView.setImageBitmap(myImage.thumbnail);



           } catch (FileNotFoundException e) {
               e.printStackTrace();
               Toast.makeText(this,"Unable to read image", Toast.LENGTH_LONG).show();

           }
       }
   }

   private void preparePreviews(){
        previewClear = Bitmap.createScaledBitmap(myImage.thumbnail, 200, 200,true);
       ImageButton filterButton_noFilter = findViewById(R.id.filter_button_noFilter);

       filterButton_noFilter.setImageBitmap(previewClear);

       ImageButton filterButton_grey = findViewById(R.id.filter_Button_grey);
       filterButton_grey.setImageBitmap(Filters.toGrayscale(previewClear));

       ImageButton filterButton_sepia = findViewById(R.id.filter_Button_sepia);
       filterButton_sepia.setImageBitmap(Filters.toSepia(previewClear));

       ImageButton filterButton_invert = findViewById(R.id.filter_Button_invert);
       filterButton_invert.setImageBitmap(Filters.invert(previewClear));

       ImageButton filterButton_polaroid = findViewById(R.id.filter_Button_polaroid);
       filterButton_polaroid.setImageBitmap(Filters.polaroid(previewClear));

       myImage.setFilter(0);
    }

   private int calculateSampleSize(int w, int h){
        int number1,number2,number,rest;
        number1 = 2;
        rest = w / number1;
        if(w <= 1000 && h <= 1000){
            return 1;
        }
        while(rest > 1000){
            rest = w/number1;
            number1++;
        }
       number2 = 2;
       rest = h / number2;
       while(rest > 1000){
           rest = h/number2;
           number2++;
       }
       if(number1 > number2){
           number = number1;
       }
       else{
           number = number2;
       }
        return number;
   }

   public void DoGrey(View view)
   {
       switch (view.getId()){
           case R.id.filter_Button_grey: {
               myImage.thumbnailWithSelectedFilter = Filters.toGrayscale(myImage.thumbnailBackup);
               imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, myImage.contrast, myImage.brightness));
               imageView.invalidate();
           }
           break;
           default: myImage.fullSizeBitmap = Filters.toGrayscale(myImage.getFullSizeBitmap());
       }
   }

    public void DoSepia(View view)
    {
        switch (view.getId()){
            case R.id.filter_Button_sepia: {
                myImage.thumbnailWithSelectedFilter = Filters.toSepia(myImage.thumbnailBackup);
                imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, myImage.contrast, myImage.brightness));
                imageView.invalidate();
            }
            break;
            default: myImage.fullSizeBitmap = Filters.toSepia(myImage.getFullSizeBitmap());
        }
    }

    public void DoInvert(View view)
    {
        switch (view.getId()){
            case R.id.filter_Button_invert: {
                myImage.thumbnailWithSelectedFilter = Filters.invert(myImage.thumbnailBackup);
                imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, myImage.contrast, myImage.brightness));
                imageView.invalidate();
            }
            break;
            default: myImage.fullSizeBitmap = Filters.invert(myImage.getFullSizeBitmap());
        }
    }

    public void DoPolaroid(View view)
    {
        switch (view.getId()){
            case R.id.filter_Button_polaroid: {
                myImage.thumbnailWithSelectedFilter = Filters.polaroid(myImage.thumbnailBackup);
                imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, myImage.contrast, myImage.brightness));
                imageView.invalidate();
            }
            break;
            default: myImage.fullSizeBitmap = Filters.polaroid(myImage.getFullSizeBitmap());
        }
    }

    public void DoNoFilter(View view){
        myImage.thumbnailWithSelectedFilter = myImage.thumbnailBackup;
        imageView.setImageBitmap(Filters.changeBitmapContrastBrightness(myImage.thumbnailWithSelectedFilter, myImage.contrast, myImage.brightness));
        imageView.invalidate();
        myImage.setFilter(0);
    }

   public void MakeVisibleContrast(View view){
        LinearLayout slidersLayout = findViewById(R.id.layoutSliders);
       slidersLayout.setVisibility(View.VISIBLE);
       HorizontalScrollView scrollView = findViewById(R.id.scroll_view);
       scrollView.setVisibility(View.INVISIBLE);
   }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicAlbumStorageDir(String albumName) {
        File file;
        if(isExternalStorageWritable()) {
            // Get the directory for the user's public pictures directory.
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumName);
            if (!file.mkdirs()) {
                Log.e("LOG_TAG: ", "Directory not created");
            }
            return file;
        }
        else {
            return null;
        }
    }


    public void saveImage(View view){

       ImageButton saveButton = findViewById(R.id.imageButton_Save);
       saveButton.setClickable(false);

       modifyFullSizeBitmap(view);
        try {
            //ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
            ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

            //File file = wrapper.getDir("Pictures", MODE_PRIVATE);
            //File dir = wrapper.getDir("MojeObrazky",MODE_PRIVATE);
            File dir = getPublicAlbumStorageDir("PhotoEditorTM");
            // Create a file to save the image
            File file;
            file = File.createTempFile(createImageName(), ".jpg",dir);

            OutputStream stream = null;


           stream = new FileOutputStream(file);


           myImage.fullSizeBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
           stream.flush();


           stream.close();

       // Parse the gallery image url to uri
       Uri savedImageURI = Uri.parse(file.getAbsolutePath());

            new MyScanner(this, file);
            Runtime.getRuntime().exec("chmod 777 " + file.getAbsolutePath());

       // Display saved image uri to TextView
       Toast.makeText(this,savedImageURI.toString(), Toast.LENGTH_LONG).show();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }
        catch(NullPointerException e){
            Toast.makeText(this,"null pointer ex here", Toast.LENGTH_LONG).show();
        }
       saveButton.setClickable(true);
   }

    private void modifyFullSizeBitmap(View view) {


            switch(myImage.filter){
                case 0: {}
                    break;
                case 1: DoGrey(view);
                    break;
                case 2: DoSepia(view);
                    break;
                case 3: DoInvert(view);
                    break;
                case 4: DoPolaroid(view);
                    break;
            }


            myImage.fullSizeBitmap = (Filters.changeBitmapContrastBrightness(myImage.fullSizeBitmap, myImage.contrast, myImage.brightness));





    }

    public void MakeVisibleFilters(View view){
        LinearLayout slidersLayout = (LinearLayout) findViewById(R.id.layoutSliders);
        slidersLayout.setVisibility(View.INVISIBLE);

        HorizontalScrollView scrollView = (HorizontalScrollView)  findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.VISIBLE);


    }

    private void enableAllEditorButtons(boolean b, int visibility ){
        ImageButton filters =   findViewById(R.id.imageButton_filters);
        filters.setEnabled(b);
        ImageButton edit =   findViewById(R.id.imageButton_contrast);
        edit.setEnabled(b);
        ImageButton save =  findViewById(R.id.imageButton_Save);
        edit.setEnabled(b);
        LinearLayout LLfilters =  findViewById(R.id.linearLayout_Filters);
        LLfilters.setVisibility(visibility);
    }


}
