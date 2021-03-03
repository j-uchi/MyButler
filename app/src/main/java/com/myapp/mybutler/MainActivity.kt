package com.myapp.mybutler

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity(),DirectoryViewer.OnActivityListener {

    private var imageDirectory:String=""

    companion object{
        private const val READ_REQUEST_CODE:Int=12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun openGallery(dir: String) {
        val intent= Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
            addCategory(Intent.CATEGORY_OPENABLE)
            type="image/*"
        }
        imageDirectory=dir
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?){
        super.onActivityResult(requestCode, resultCode, resultData)

        if(resultCode!=RESULT_OK)return

        when(requestCode){
            READ_REQUEST_CODE -> {
                resultData?.data?.also {
                    val inputStream = contentResolver.openInputStream(it)
                    OutputImage(BitmapFactory.decodeStream(inputStream))
                }
            }
        }

    }

    fun OutputImage(image: Bitmap){
        if(imageDirectory=="")return

        val time:String= SystemClock.uptimeMillis().toString()

        val file = File("$imageDirectory/$time.png")
        val outStream = FileOutputStream(file)
        image.compress(CompressFormat.PNG, 100, outStream)
        outStream.close()
        imageDirectory=""
    }

}