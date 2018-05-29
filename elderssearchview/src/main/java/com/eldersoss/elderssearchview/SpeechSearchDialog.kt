/*
 * Copyright (c) 2018. Elders LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eldersoss.elderssearchview

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by IvanVatov on 5/16/2018.
 */
internal class SpeechSearchDialog(private val activity: Activity, private var listener: SpeechSearchListener?, private val logoImageResource: Int) : RecognitionListener {

    interface SpeechSearchListener {
        fun onTextResult(text: String)
    }

    private var dialog: Dialog? = null

    private var speechRecognizer: SpeechRecognizer? = null

    private var bigHeaderText: TextView? = null
    private var smallHeaderText: TextView? = null
    private var tapToSpeechButton: ImageButton? = null
    private var logoImageView: ImageView? = null


    init {
        val permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    777)
        } else {
            buildDialog()
        }
    }

    private fun buildDialog() {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.esv_dialog_speech_search)
        dialog?.window?.setLayout((activity.resources.displayMetrics.widthPixels * 0.90).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        bigHeaderText = dialog?.findViewById(R.id.big_header_text)
        smallHeaderText = dialog?.findViewById(R.id.small_header_text)
        tapToSpeechButton = dialog?.findViewById(R.id.tap_to_speech_button)
        logoImageView = dialog?.findViewById(R.id.speech_dialog_logo)

        logoImageView?.setImageResource(logoImageResource)

        tapToSpeechButton?.setOnClickListener {
            tapToSpeechButton?.isActivated = true
            startListening()
        }

        dialog?.setOnShowListener {
            if (SpeechRecognizer.isRecognitionAvailable(activity)) {
                startListening()
            }
        }
        dialog?.setOnDismissListener {
            listener = null
            speechRecognizer?.destroy()
        }

        dialog?.show()
    }

    private fun startListening() {
        smallHeaderText?.visibility = View.INVISIBLE
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
        speechRecognizer?.setRecognitionListener(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity.packageName)
        speechRecognizer?.startListening(intent)
    }


    override fun onReadyForSpeech(params: Bundle?) {
        bigHeaderText?.setText(R.string.speech_search_try_saying_something)
        tapToSpeechButton?.isEnabled = false

    }

    override fun onRmsChanged(rmsdB: Float) {

    }

    override fun onBufferReceived(buffer: ByteArray?) {

    }

    override fun onPartialResults(partialResults: Bundle?) {

    }

    override fun onEvent(eventType: Int, params: Bundle?) {

    }

    override fun onBeginningOfSpeech() {

    }

    override fun onEndOfSpeech() {

    }

    override fun onError(error: Int) {
        tapToSpeechButton?.isEnabled = true
        when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> {
                speechRecognizer?.destroy()
                bigHeaderText?.setText(R.string.speech_search_didnt_catch_that)
                smallHeaderText?.visibility = View.VISIBLE
            }
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                dialog?.dismiss()
            }
            else -> {
                speechRecognizer?.destroy()
                bigHeaderText?.setText(R.string.speech_search_not_available)
                smallHeaderText?.visibility = View.VISIBLE
            }
        }
    }

    override fun onResults(results: Bundle?) {
        val resultList = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (resultList != null && resultList.size > 0) {
            listener?.onTextResult(resultList[0].toString())
            dialog?.dismiss()
        }
    }


}