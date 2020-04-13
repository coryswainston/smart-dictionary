package com.coryswainston.smart.dictionary.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import com.coryswainston.smart.dictionary.R
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment.OnFragmentInteractionListener
import com.coryswainston.smart.dictionary.services.DetectorProcessor
import com.coryswainston.smart.dictionary.util.Settings
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.text.Text
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {
    private var cameraSource: CameraSource? = null
    private var surfaceAvailable = false

    private var definitionsFragment: DefinitionsFragment? = null

    private var detectedText: String? = null
    private var blocks: List<Text>? = null
    private var selectedWord: String? = null
    private var activeEvent: MotionEvent? = null
    private val ratio = 0.9f

    private lateinit var bgButtonNormal: Drawable
    private lateinit var bgButtonTouch: Drawable

    @Suppress("deprecation") // for resources.getDrawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bgButtonNormal = resources.getDrawable(R.drawable.define_button_bg)
        bgButtonTouch = resources.getDrawable(R.drawable.define_button_pressed_bg)

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        define_button.scaleX = 0f
        define_button.scaleY = 0f
        loading_gif.visibility = View.GONE

        if (checkForPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET)) {
            surfaceView.holder.addCallback(SurfaceCallback())
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            surfaceAvailable = true
            try {
                setUpCamera()
            } catch (e: SecurityException) {
                Log.e("surfaceCallback", e.toString())
            } catch (e: IOException) {
                Log.e("surfaceCallback", e.toString())
            }

            val captureText = View.OnClickListener {
                loading_gif.visibility = View.VISIBLE
                val intent = Intent(this@MainActivity, DefineActivity::class.java)
                intent.putExtra("detections", detectedText)
                startActivity(intent)
            }

            capture_button.setOnClickListener(captureText)
            surfaceView.setOnTouchListener(object : OnTouchListener {

                private var buzzed = false

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    return when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            if (eventTouchesDefineButton(event)) {
                                define_button.background = bgButtonTouch

                                if (!buzzed) {
                                    define_button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    buzzed = true
                                }
                                return true
                            } else {
                                define_button.background = bgButtonNormal
                                buzzed = false
                            }
                            activeEvent = event
                            val screenX = event.x
                            val screenY = event.y
                            val realX = screenX * ratio
                            val realY = screenY * ratio
                            for (block in blocks!!) {
                                if (block.boundingBox.contains(realX.toInt(), realY.toInt())) {
                                    val tempWord = selectedWord
                                    selectedWord = trimText(block)
                                    if (tempWord == null || tempWord != selectedWord) {
                                        bringUpDefineButton(event)
                                    }
                                    break
                                }
                            }
                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            define_button.animate().scaleX(0f).scaleY(0f).duration = 300
                            if (eventTouchesDefineButton(event)) {
                                define_button.background = bgButtonNormal
                                buzzed = false
                                define_button.performClick()
                            } else {
                                activeEvent = null
                                selectedWord = null
                            }
                            true
                        }
                        else -> false
                    }
                }
            })
        }

        private fun bringUpDefineButton(event: MotionEvent) {
            define_button.text = selectedWord
            var x = event.x + surfaceView!!.left - define_button.width.toFloat() / 2f
            if (x < 0) {
                x = 0f
            }
            if (x > surfaceView!!.right - define_button.width) {
                x = surfaceView!!.right - define_button.width.toFloat()
            }
            val y = event.y + surfaceView!!.top - define_button.height.toFloat() - 80
            if (define_button.scaleX == 0f) {
                define_button.x = x
                define_button.y = y
                define_button.transformationMethod = null
                define_button.animate().scaleX(1f).scaleY(1f).duration = 300
            } else {
                define_button.animate().translationX(x)
                define_button.animate().translationY(y)
            }
        }

        private fun eventTouchesDefineButton(event: MotionEvent): Boolean {
            if (define_button.visibility == View.VISIBLE && define_button.scaleX == 1f) {
                val coords = IntArray(2)
                define_button.getLocationInWindow(coords)
                val buttonPos = Rect(coords[0],
                        coords[1],
                        coords[0] + define_button.width,
                        coords[1] + define_button.height)
                if (buttonPos.contains(event.x.toInt() + surfaceView!!.left,
                                event.y.toInt() + surfaceView!!.top)) {
                    return true
                }
            }
            return false
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    @Suppress("unused_parameter")
    fun onDefineButtonClick(v: View?) {
        if (definitionsFragment == null) {
            definitionsFragment = DefinitionsFragment.newInstance(selectedWord)
        } else {
            definitionsFragment!!.addWord(selectedWord)
        }
        definitionsFragment!!.show(this)
        activeEvent = null
        selectedWord = null
    }

    private fun trimText(block: Text?): String? {
        if (block == null || activeEvent == null) {
            return null
        }
        val fullText = block.value
        val width = block.boundingBox.width()
        val distanceToTouch = (activeEvent!!.x * ratio).toInt() - block.boundingBox.left
        val howFarInAreWe = distanceToTouch / width.toFloat()
        val textLength = fullText.length
        var approximateIndex = (textLength * howFarInAreWe).toInt()
        if (!Character.isLetter(fullText[approximateIndex])) {
            var i = 1
            while (approximateIndex - i >= 0 || approximateIndex + i < fullText.length) {
                if (approximateIndex - i >= 0 && Character.isLetter(fullText[approximateIndex - i])) {
                    approximateIndex -= i
                    break
                }
                if (approximateIndex + i < fullText.length && Character.isLetter(fullText[approximateIndex + i])) {
                    approximateIndex += i
                    break
                }
                i++
            }
        }
        var startIndex = approximateIndex
        var endIndex = approximateIndex
        run {
            var i = approximateIndex
            while (i < textLength && Character.isLetter(fullText[i])) {
                endIndex++
                i++
            }
        }
        var i = approximateIndex
        while (i > 0 && Character.isLetter(fullText[i - 1])) {
            startIndex--
            i--
        }
        return fullText.substring(startIndex, endIndex).toLowerCase(Locale.getDefault())
    }

    private fun checkForPermissions(vararg permissions: String): Boolean {
        val permissionsToRequest: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isEmpty()) {
            return true
        }
        ActivityCompat.requestPermissions(this@MainActivity,
                permissionsToRequest.toTypedArray(), 0)
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            results: IntArray) {
        if (requestCode != 0) {
            return
        }
        for (i in results.indices) {
            if (results[i] != PackageManager.PERMISSION_GRANTED) {
                val alertDialog = AlertDialog.Builder(this@MainActivity).create()
                alertDialog.setTitle("Permissions needed")
                alertDialog.setMessage("This app requires camera permissions to function properly.")
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK"
                ) { dialog, _ ->
                    dialog.dismiss()
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET
                    ),
                            0)
                }
                alertDialog.show()
                return
            }
        }
        surfaceView!!.holder.addCallback(SurfaceCallback())
    }

    @Throws(IOException::class, SecurityException::class)
    private fun setUpCamera() {
        val recognizer = TextRecognizer.Builder(applicationContext).build()
        recognizer.setProcessor(DetectorProcessor().withCallback { blocks, detectedText ->
            this@MainActivity.detectedText = detectedText
            this@MainActivity.blocks = blocks
            if (activeEvent != null) {
                surfaceView!!.onTouchEvent(activeEvent)
            }
        })
        if (!recognizer.isOperational) {
            Log.w(TAG, "not operational")
        }
        cameraSource = CameraSource.Builder(this, recognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(0.1f)
                .setRequestedPreviewSize(DESIRED_WIDTH, DESIRED_HEIGHT)
                .setAutoFocusEnabled(true)
                .build()
        startCameraSource()
    }

    @Throws(IOException::class, SecurityException::class)
    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Play Services Unavailable", Toast.LENGTH_SHORT).show()
        }
        if (surfaceAvailable) {
            Log.d(TAG, "surface was available")
            cameraSource!!.start(surfaceView!!.holder)
            Log.d(TAG, "started cameraSource")
        } else {
            Log.d(TAG, "surface wasn't available")
        }
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        loading_gif.visibility = View.GONE
        if (cameraSource != null) {
            try {
                startCameraSource()
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
            }
        }
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        if (cameraSource != null) {
            cameraSource!!.stop()
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource!!.release()
        }
    }

    /**
     * Called when the user hits the exit button in the corner.
     */
    override fun onDefinitionFragmentExit(v: View) {
        if (definitionsFragment!!.onExit()) {
            definitionsFragment = null
        }
    }

    /**
     * Called when the user hits the settings button.
     */
    @Suppress("unused_parameter")
    fun onSettingsClick(v: View?) {
        Settings.showDialog(this)
    }

    override fun toggleWordEdit(v: View) {
        definitionsFragment!!.toggleWordEdit(v)
    }

    override fun onTabClick(v: View) {
        definitionsFragment!!.onTabClick(v)
    }

    override fun onGoogleSearch(v: View) {
        definitionsFragment!!.onGoogleSearch()
    }

    override fun onWikipediaSearch(v: View) {
        definitionsFragment!!.onWikipediaSearch()
    }

    override fun onWordsBackOrForward(v: View) {
        definitionsFragment!!.onWordsBackOrForward(v)
    }

    override fun onBackPressed() {
        if (definitionsFragment != null) {
            definitionsFragment!!.remove()
            definitionsFragment = null
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val DESIRED_WIDTH = 1200
        private const val DESIRED_HEIGHT = 1200
    }
}