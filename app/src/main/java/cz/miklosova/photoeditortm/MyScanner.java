package cz.miklosova.photoeditortm;

import java.io.File;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

/**
 * This class should scan the image to be visible in gallery.
 */
public class MyScanner implements MediaScannerConnectionClient {

    private MediaScannerConnection connection;
    private File file;

    MyScanner(Context context, File f) {
        file = f;
        connection = new MediaScannerConnection(context, this);
    }
    @Override
    public void onMediaScannerConnected() {

        connection.scanFile(file.toString(), "image/jpeg");
    }

    public void scan() {
        connection.connect();

    }
    @Override
    public void onScanCompleted(String path, Uri uri) {
        connection.disconnect();
    }

}
