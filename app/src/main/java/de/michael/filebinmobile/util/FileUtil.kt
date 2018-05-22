package de.michael.filebinmobile.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File

const val COLUMN_NAME_DISPLAY_NAME = "_display_name"

object FileUtil {

    fun createFileCopyFromUri(uri: Uri, context: Context): File {
        val displayName = getDisplayName(uri, context.contentResolver)

        // read from queried file
        val inputStream = context.contentResolver.openInputStream(uri)
        // write to new file in app context (copy)
        val fileOutputStream = context.openFileOutput(displayName, Context.MODE_PRIVATE)


        val buf = ByteArray(1024)
        var len = inputStream.read(buf)

        while(len > 0) {
            fileOutputStream.write(buf, 0, len)
            len = inputStream.read(buf)
        }

        inputStream.close()
        fileOutputStream.close()

        return File("${context.filesDir}${File.separator}$displayName")
    }

    private fun getDisplayName(uri: Uri, contentResolver: ContentResolver): String {
        var displayName = "tmp"

        val cursor: Cursor = contentResolver.query(uri, null, null, null, null)

        if (cursor.moveToFirst()) {
            // Get column index of display name
            val columnIndex = cursor.getColumnIndex(COLUMN_NAME_DISPLAY_NAME)

            // Get column value
            if (columnIndex >= 0) {
                displayName = cursor.getString(columnIndex)
            }
        }

        cursor.close()

        return displayName
    }

    fun getMimeType(file: File): String? {
        val extension: String = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}