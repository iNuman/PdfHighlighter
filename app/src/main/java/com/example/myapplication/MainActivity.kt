package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.Constants.DATA_CLASS_BUNDLE
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        copyFileFromAssetsToInternal(this, "example.pdf")
        copyFileFromAssetsToInternal(this, "Android.pdf")
        startActivity(Intent(this@MainActivity, ViewEditPdfActivity::class.java).apply {
        //"/data/user/0/com.example.myapplication/files/example.pdf"
            putExtra(Constants.PDF_FILE_PATH, "${filesDir.path}/Android.pdf")
//            putExtra(Constants.PDF_FILE_PATH, "${filesDir.path}/example.pdf")
//            putExtra(Constants.PDF_FILE_PATH, "https://www.clickdimensions.com/links/TestPDFfile.pdf")
            putExtra(Constants.DOC_ID, -1L)
            putExtra(Constants.DIRECT_DOC_EDIT_OPEN, false)
//            putParcelableArrayListExtra(DATA_CLASS_BUNDLE)
            putExtra(Constants.DOC_NAME, "Android.pdf")
//            putExtra(Constants.DOC_NAME, "example.pdf")
        })
    }

    private fun copyFileFromAssetsToInternal(context: Context, assetFileName: String): String? {
        val inputStream: InputStream
        val outputStream: OutputStream
        try {
            inputStream = context.assets.open(assetFileName)
            val outputFile = File(context.filesDir, assetFileName)
            outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            return outputFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}