package com.wakeup.hyperion.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.util.regex.Pattern

object FileUtils {
    const val QUERY = "=?"
    const val FIRST_INDEX = 0
    const val PATH_FILE_REGEX = "(\\d+)$"

    /**
     * @param contentResolver content resolver get from context
     * @param uri      image's uri from onActivityResult
     * @return real path of image
     */
    fun getRealPathFromURI(context: Context, contentResolver: ContentResolver, uri: Uri?): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return getRealPathFromURIApi29AndLower(
                context,
                contentResolver,
                uri
            )
        }
        return getRealPathFromURIApi29AndHigher(
            context,
            contentResolver,
            uri
        )
    }

    private fun getRealPathFromURIApi29AndLower(
        context: Context, contentResolver: ContentResolver,
        contentUri: Uri?
    ): String {
        val projection = arrayOf("_data")
        var result = ""
        contentUri?.let {
            val cursor = contentResolver.query(contentUri, projection, null, null, null)
            if (cursor != null) {
                result = try {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    cursor.getString(columnIndex)
                } catch (ex: Exception) {
                    getPathFromUri(context, contentUri).toString()
                } finally {
                    cursor.close()
                }
            }
            cursor?.close()
        }
        return result
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        when {
            isKitKat && DocumentsContract.isDocumentUri(context, uri) -> // ExternalStorageProvider
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split =
                            docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val type = split[0]

                        if ("primary".equals(type, ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory()
                                .toString() + "/" + split[1]
                        }

                    }
                    isDownloadsDocument(uri) -> {

                        val id = DocumentsContract.getDocumentId(uri)
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )

                        return getDataColumn(
                            context,
                            contentUri,
                            null,
                            null
                        )
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split =
                            docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val type = split[0]

                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }

                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])

                        return getDataColumn(
                            context,
                            contentUri,
                            selection,
                            selectionArgs
                        )
                    }// MediaProvider
                    // DownloadsProvider
                }// MediaProvider
            // DownloadsProvider
            "content".equals(uri.scheme!!, ignoreCase = true) -> // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                    context, uri, null,
                    null
                )
            "file".equals(uri.scheme!!, ignoreCase = true) -> return uri.path
        }// File
        // MediaStore (and general)
        // File
        // MediaStore (and general)

        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getRealPathFromURIApi29AndHigher(
        context: Context, contentResolver: ContentResolver,
        uri: Uri?
    ): String {
        var filePath = ""
        val pattern = Pattern.compile(PATH_FILE_REGEX)
        val matcher = pattern.matcher(uri.toString())
        if (!matcher.find()) {
            return filePath
        }
        val imageId = matcher.group()
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + QUERY
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(imageId), null
        )
        val columnIndex = cursor!!.getColumnIndex(column[FIRST_INDEX])
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        if (filePath.isEmpty()) {
            filePath =
                getRealPathFromURIApi29AndLower(
                    context,
                    contentResolver,
                    uri
                )
        }
        return filePath
    }
}