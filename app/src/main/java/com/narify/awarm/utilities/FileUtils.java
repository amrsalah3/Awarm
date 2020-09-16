package com.narify.awarm.utilities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import com.narify.awarm.app.AppContext;

import java.io.File;

public class FileUtils {

    public static boolean isAudioUri(Uri uri) {
        try {
            String fileType = getMimeType(uri).substring(0, 5);
            return fileType.equals("audio");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    private static String getMimeType(Uri uri) throws NullPointerException {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            mimeType = AppContext.get().getContentResolver().getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }

        return mimeType;
    }

    public static Uri getPersistableUriFromPath(String uriStringPath) {
        Uri ringtoneUri = Uri.parse(uriStringPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            AppContext.get()
                    .getContentResolver()
                    .takePersistableUriPermission(ringtoneUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return ringtoneUri;
    }

    public static String getFileName(Uri uri) throws NullPointerException {
        String result = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            try (Cursor cursor = AppContext.get().getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

}
