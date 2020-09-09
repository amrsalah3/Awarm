package com.narify.awarm.utilities;

import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.narify.awarm.app.AppContext;

import java.io.File;

public class FileUtils {

    public static String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
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
