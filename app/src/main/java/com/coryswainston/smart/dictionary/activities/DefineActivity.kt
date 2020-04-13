package com.coryswainston.smart.dictionary.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.coryswainston.smart.dictionary.R
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment.OnFragmentInteractionListener
import com.coryswainston.smart.dictionary.listeners.WordGrabber
import com.coryswainston.smart.dictionary.util.Settings
import kotlinx.android.synthetic.main.activity_define.*

class DefineActivity : AppCompatActivity(), OnFragmentInteractionListener {
    private var definitionsFragment: DefinitionsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_define)
        detect_view.setOnTouchListener(WordGrabber { text ->
            if (definitionsFragment == null) {
                definitionsFragment = DefinitionsFragment.newInstance(text)
            } else {
                definitionsFragment!!.addWord(text)
            }
            definitionsFragment!!.show(this@DefineActivity)
        })

        detect_view.text = intent.getStringExtra("detections")
        detect_view.movementMethod = ScrollingMovementMethod()
    }

    /**
     * Called when the user hits the back button.
     */
    @Suppress("unused_parameter")
    fun onBack(v: View?) {
        onBackPressed()
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
}