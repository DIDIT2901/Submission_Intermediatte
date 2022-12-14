package com.dicoding.picodiploma.loginwithanimation

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val FILENAME_FORMAT = "dd-MMM-yyyy"

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .into(this)
}

fun getTimeUpload(timeStamp: String): String {
    val currentTime = getCurrentDate()
    val uploadTime = parseUTCDate(timeStamp)
    val diff: Long = currentTime.time - uploadTime.time
    val second = diff / 1000
    val minutes = second / 60
    val hours = minutes / 60
    val days = hours / 24
    val label = when (minutes.toInt()) {
        0 -> "$second Second Ago"
        in 1..59 -> "$minutes Minutes Ago"
        in 60..1440 -> "$hours Hours Ago"
        else -> "$days Days Ago"
    }
    return label
}

private fun getCurrentDate(): Date = Date()

private fun parseUTCDate(timeStamp: String): Date =
    try {
        val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(timeStamp) as Date
    } catch (e: ParseException) {
        getCurrentDate()
    }

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}