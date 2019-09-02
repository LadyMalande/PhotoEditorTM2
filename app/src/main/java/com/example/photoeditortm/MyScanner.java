package com.example.photoeditortm;

import java.io.File;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class MyScanner implements MediaScannerConnectionClient {

    private MediaScannerConnection connection;
    private File file;

    MyScanner(Context context, File f) {
        file = f;
        connection = new MediaScannerConnection(context, this);
        connection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        connection.scanFile(file.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        connection.disconnect();
    }

}
