package com.myapp.mybutler

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity(),DirectoryViewer.OnActivityListener,LockFragment.OnLockListener {

    private var imageDirectory:String=""

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    companion object{
        private const val READ_REQUEST_CODE:Int=12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onResume(){
        super.onResume()
        Handler(Looper.getMainLooper()).post{
            findNavController(R.id.nav_host_fragment).navigate(R.id.LockFragment)
        }
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

    private fun createBiometricPrompt(): BiometricPrompt {
        executor = ContextCompat.getMainExecutor(this)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                ).show()
                findNavController(R.id.nav_host_fragment).popBackStack()
            }
        }
        biometricPrompt = BiometricPrompt(this, executor, callback)
        return biometricPrompt
    }

    private fun showId() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("指紋認証")
                .setSubtitle("サンプルです")
                .setConfirmationRequired(true)
                .setDeviceCredentialAllowed(true)
                .build()
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onUnlockDialog() {
        createBiometricPrompt()
        showId()
    }

}