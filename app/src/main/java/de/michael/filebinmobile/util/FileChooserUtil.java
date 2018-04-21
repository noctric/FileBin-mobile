package de.michael.filebinmobile.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileChooserUtil {


    public static File createFileCopyFromUri(@NonNull Uri uri, @NonNull Context context) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        String fileDisplayName = getDisplayName(uri, contentResolver);

        try (InputStream in = contentResolver.openInputStream(uri)) {
            try (FileOutputStream out = context.openFileOutput(fileDisplayName, Context.MODE_PRIVATE)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }

        String filePath = context.getFilesDir() + File.separator + fileDisplayName;

        return new File(filePath);
    }


    private static String getDisplayName(Uri uri, ContentResolver contentResolver) {

        String displayName = "tmp";

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                String columnName = "_display_name";

                // Get column index of display name
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value
                if (imageColumnIndex >= 0) {
                    displayName = cursor.getString(imageColumnIndex);
                }

            }

            cursor.close();
        }

        return displayName;
    }

}
