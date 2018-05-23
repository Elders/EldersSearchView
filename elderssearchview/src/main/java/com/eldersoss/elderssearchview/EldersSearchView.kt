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

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher

import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*

/**
 * Created by IvanVatov on 5/16/2018.
 *
 * EldersSearchView is the most easiest to be used search view for Android
 *
 */
class EldersSearchView : RelativeLayout, SpeechSearchDialog.SpeechSearchListener {

    private var searchListener: ((word: String) -> Unit)? = null

    private var backListener: (() -> String?)? = null

    // Attributes and their default values
    private var esvHintText = ""
    private var esvHintTextColor = -6710887 // Hex #999999
    private var esvIconsColor = -9211533 // Hex #737173
    private var esvIconsWidth = dpToPixels(48)
    private var esvSuggestionsFileName = "SuggestionsFile"
    private var esvSearchBarHeight = dpToPixels(48)
    private var esvElevation = dpToPixels(2).toFloat()
    private var esvMargin = dpToPixels(7)
    private var esvBackground = R.drawable.elders_search_bar_background
    private var esvSuggestionsBackground = R.drawable.elders_search_bar_background
    private var esvSpeechRecognizerLogo = R.drawable.elders_logo


    private var searchSuggestionsAdapter: SearchSuggestionsAdapter? = null

    private val searchViewLayout = RelativeLayout(context)
    private val suggestionsViewLayout = RelativeLayout(context)

    private val linearLayoutLeft = LinearLayout(context)
    private val imageButtonBack = ImageView(context)
    private val imageButtonSearch = ImageView(context)

    private val searchEditText = EditText(context)
    private val searchHint = TextView(context)

    private val linearLayoutRight = LinearLayout(context)
    private val imageButtonClose = ImageView(context)
    private val imageButtonSpeech = ImageView(context)
    val filterButton = ImageView(context)

    private val suggestionsListView = ListView(context)

    private val showAnimation = AnimationUtils.loadAnimation(context, R.anim.suggestions_animaiton_show)
    private val hideAnimation = AnimationUtils.loadAnimation(context, R.anim.suggestions_animaiton_hide)

    private var currentSearchPhrase: String? = null

    private val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val customAttributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.EldersSearchView,
                0, 0)

        for (i in 0 until customAttributes.indexCount) {
            val a = customAttributes.getIndex(i)
            when (a) {
                R.styleable.EldersSearchView_esvHintText -> {
                    esvHintText = customAttributes.getString(a)
                }
                R.styleable.EldersSearchView_esvIconsColor -> {
                    esvIconsColor = customAttributes.getColor(a, esvIconsColor)
                }
                R.styleable.EldersSearchView_esvSuggestionsFileName -> {
                    esvSuggestionsFileName = customAttributes.getString(a)
                }
                R.styleable.EldersSearchView_esvSearchViewHeight -> {
                    esvSearchBarHeight = customAttributes.getDimensionPixelSize(a, esvSearchBarHeight)
                }
                R.styleable.EldersSearchView_esvIconsWidth -> {
                    esvIconsWidth = customAttributes.getDimensionPixelSize(a, esvIconsWidth)
                }
                R.styleable.EldersSearchView_esvHintTextColor -> {
                    esvHintTextColor = customAttributes.getColor(a, esvHintTextColor)
                }
                R.styleable.EldersSearchView_esvElevation -> {
                    esvElevation = customAttributes.getDimensionPixelSize(a, esvElevation.toInt()).toFloat()
                }
                R.styleable.EldersSearchView_esvBackground -> {
                    esvBackground = customAttributes.getResourceId(a, esvBackground)
                }
                R.styleable.EldersSearchView_esvSuggestionsBackground -> {
                    esvSuggestionsBackground = customAttributes.getResourceId(a, esvSuggestionsBackground)
                }
                R.styleable.EldersSearchView_esvMargin -> {
                    esvMargin = customAttributes.getDimensionPixelSize(a, esvMargin)
                }
                R.styleable.EldersSearchView_esvSpeechRecognizerLogo -> {
                    esvSpeechRecognizerLogo = customAttributes.getResourceId(a, esvSpeechRecognizerLogo)
                }
            }
        }

        customAttributes.recycle()

        searchSuggestionsAdapter = SearchSuggestionsAdapter((context as Activity), { searchForText(it) }, esvSuggestionsFileName, esvIconsColor, esvIconsWidth)
        suggestionsListView.adapter = searchSuggestionsAdapter

        applyStyles()
        initViewState()
        initListeners()

        setupAnimations()
    }

    fun setOnSearchListener(listener: ((phrase: String) -> Unit)?) {
        searchListener = listener
    }

    fun setOnBackListener(listener: (() -> String?)?) {
        backListener = listener
    }

    fun clickBackButton() {
        backClicked()
    }

    fun setSearchedPhrase(phrase: String) {
        val searchPhrase = phrase.trim()
        imageButtonSearch.visibility = View.GONE
        imageButtonBack.visibility = View.VISIBLE
        searchHint.visibility = View.GONE
        imageButtonSpeech.visibility = View.GONE
        searchEditText.setText(searchPhrase, TextView.BufferType.EDITABLE)
        filterButton.visibility = View.VISIBLE
        currentSearchPhrase = searchPhrase
    }

    fun searchForPhrase(phrase: String) {
        if (phrase.isNotBlank()) {
            imageButtonSearch.visibility = View.GONE
            searchHint.visibility = View.GONE
            imageButtonSpeech.visibility = View.GONE
            searchEditText.setText(phrase, TextView.BufferType.EDITABLE)
            searchForText(phrase)
        }
    }

    override fun onTextResult(text: String) {
        searchForPhrase(text)
    }

    private fun initViewState() {
        filterButton.visibility = View.GONE
        imageButtonClose.visibility = View.GONE
        imageButtonBack.visibility = View.GONE
        searchEditText.clearFocus()
    }

    private fun applyStyles() {
        searchViewLayout.isFocusable = true
        searchViewLayout.isFocusableInTouchMode = true

        val searchViewLayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        searchViewLayoutParams.setMargins(
                esvMargin,
                esvMargin,
                esvMargin,
                esvMargin
        )

        searchViewLayout.layoutParams = searchViewLayoutParams
        searchViewLayout.setBackgroundResource(esvBackground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchViewLayout.elevation = esvElevation
        }

        this.addView(searchViewLayout)

        // Build views tree
        linearLayoutLeft.id = View.generateViewId()
        linearLayoutLeft.addView(imageButtonBack)
        linearLayoutLeft.addView(imageButtonSearch)

        linearLayoutRight.id = View.generateViewId()
        linearLayoutRight.addView(imageButtonClose)
        linearLayoutRight.addView(imageButtonSpeech)
        linearLayoutRight.addView(filterButton)

        searchViewLayout.addView(linearLayoutLeft)

        searchViewLayout.addView(searchEditText)
        searchViewLayout.addView(searchHint)

        searchViewLayout.addView(linearLayoutRight)

        this.addView(suggestionsViewLayout)
        suggestionsViewLayout.addView(suggestionsListView)

        val imageButtonsParams = LinearLayout.LayoutParams(
                esvIconsWidth,
                esvSearchBarHeight
        )

        // style left side
        linearLayoutLeft.orientation = LinearLayout.HORIZONTAL
        imageButtonBack.layoutParams = imageButtonsParams
        imageButtonBack.scaleType = ImageView.ScaleType.CENTER
        imageButtonBack.setImageResource(R.drawable.material_icon_arrow_left)
        imageButtonBack.setColorFilter(esvIconsColor)

        imageButtonSearch.layoutParams = imageButtonsParams
        imageButtonSearch.scaleType = ImageView.ScaleType.CENTER
        imageButtonSearch.setImageResource(R.drawable.material_icon_magnify)
        imageButtonSearch.setColorFilter(esvIconsColor)

        // style input field
        searchHint.text = esvHintText
        searchHint.textSize = 18F
        searchHint.ellipsize = TextUtils.TruncateAt.END
        searchHint.maxLines = 1
        val searchHintParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                esvSearchBarHeight
        )
        searchHintParams.addRule(RelativeLayout.RIGHT_OF, linearLayoutLeft.id)
        searchHint.layoutParams = searchHintParams
        searchHint.gravity = Gravity.CENTER_VERTICAL
        searchHint.setTextColor(esvHintTextColor)


        val searchEditTextParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                esvSearchBarHeight
        )
        searchEditTextParams.addRule(RelativeLayout.RIGHT_OF, linearLayoutLeft.id)
        searchEditTextParams.addRule(RelativeLayout.LEFT_OF, linearLayoutRight.id)
        searchEditText.layoutParams = searchEditTextParams
        searchEditText.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchEditText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        searchEditText.isLongClickable = false
        searchEditText.maxLines = 1
        searchEditText.setSingleLine(true)
        searchEditText.setTextIsSelectable(false)
        searchEditText.setBackgroundColor(0)
        searchEditText.gravity = Gravity.CENTER_VERTICAL
        val fixPadding = (searchEditText.paddingBottom + searchEditText.paddingTop) / 2
        searchEditText.setPadding(0, fixPadding, 0, fixPadding)

        // style right side
        (linearLayoutRight.layoutParams as LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

        imageButtonClose.layoutParams = imageButtonsParams
        imageButtonClose.scaleType = ImageView.ScaleType.CENTER
        imageButtonClose.setImageResource(R.drawable.material_icon_close)
        imageButtonClose.setColorFilter(esvIconsColor)

        imageButtonSpeech.layoutParams = imageButtonsParams
        imageButtonSpeech.scaleType = ImageView.ScaleType.CENTER
        imageButtonSpeech.setImageResource(R.drawable.material_icon_microphone)
        imageButtonSpeech.setColorFilter(esvIconsColor)

        filterButton.layoutParams = imageButtonsParams
        filterButton.scaleType = ImageView.ScaleType.CENTER
        filterButton.setImageResource(R.drawable.material_icon_filter)
        filterButton.setColorFilter(esvIconsColor)

        // style suggestions
        val suggestionsViewLayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        suggestionsViewLayoutParams.setMargins(
                0,
                esvSearchBarHeight,
                0,
                0
        )
        suggestionsViewLayout.layoutParams = suggestionsViewLayoutParams

        val suggestionsListViewLayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        suggestionsListViewLayoutParams.setMargins(
                esvMargin,
                esvMargin,
                esvMargin,
                esvMargin
        )
        suggestionsListView.setBackgroundResource(esvBackground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            suggestionsListView.elevation = esvElevation
            suggestionsListView.z = esvElevation - 1f
        }
        suggestionsListView.divider = null
        suggestionsListView.layoutParams = suggestionsListViewLayoutParams
        suggestionsListView.visibility = View.GONE
    }

    private fun initListeners() {
        imageButtonSpeech.setOnClickListener {
            try {
                SpeechSearchDialog(context as Activity, this, esvSpeechRecognizerLogo)
            } catch (e: ClassCastException) {
                Log.e(this.javaClass.name, e.message)
            }
        }

        imageButtonSearch.setOnClickListener { searchEditText.requestFocus() }

        searchEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = searchEditText.text.toString()
                if (searchText.isNotBlank()) {
                    searchForText(searchText)
                    imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                    searchEditText.clearFocus()
                }
                return@OnEditorActionListener true
            }
            false
        })

        searchEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startSearching()
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (start + after == 0) {
                    searchHint.visibility = View.VISIBLE
                    imageButtonSpeech.visibility = View.VISIBLE
                    imageButtonClose.visibility = View.GONE
                } else {
                    searchHint.visibility = View.GONE
                    imageButtonSpeech.visibility = View.GONE
                    imageButtonClose.visibility = View.VISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    searchSuggestionsAdapter?.filterItems(s)
                }
            }
        })

        imageButtonClose.setOnClickListener({
            searchEditText.text = null
            searchEditText.requestFocus()
            showSearchSuggestions()
        })

        imageButtonBack.setOnClickListener({ backClicked() })
    }

    private fun backClicked() {
        if (suggestionsListView.visibility == View.VISIBLE) {
            hideSearchSuggestions()
            if (currentSearchPhrase == null) {
                closeSearching()
            } else {

            }
            return
        }
        val w = backListener?.invoke()
        if (w == null) {
            currentSearchPhrase = null
            closeSearching()
        } else {
            currentSearchPhrase = w
            searchEditText.setText(w, TextView.BufferType.EDITABLE)
            filterButton.visibility = View.VISIBLE
        }
    }

    private fun startSearching() {
        showSearchSuggestions()
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        if (searchEditText.text.toString().isEmpty()) {
            searchHint.visibility = View.VISIBLE
            imageButtonSpeech.visibility = View.VISIBLE
            imageButtonClose.visibility = View.GONE
        } else {
            searchHint.visibility = View.GONE
            imageButtonSpeech.visibility = View.GONE
            imageButtonClose.visibility = View.VISIBLE
        }
        imageButtonSearch.visibility = View.GONE
        imageButtonBack.visibility = View.VISIBLE
    }

    private fun closeSearching() {
        searchEditText.clearFocus()
        searchEditText.text = null
        imageButtonBack.visibility = View.GONE
        imageButtonSearch.visibility = View.VISIBLE
        searchHint.visibility = View.VISIBLE
        imageButtonClose.visibility = View.GONE
        imageButtonSpeech.visibility = View.VISIBLE
        filterButton.visibility = View.GONE
    }

    private fun searchForText(text: String) {
        val searchText = text.trim()
        searchEditText.setText(searchText, TextView.BufferType.EDITABLE)
        filterButton.visibility = View.VISIBLE
        currentSearchPhrase = searchText
        hideSearchSuggestions()
        searchListener?.invoke(searchText)
        searchSuggestionsAdapter?.addSearch(searchText)
    }

    private fun showSearchSuggestions() {
        if (suggestionsListView.visibility != View.VISIBLE) {
            searchSuggestionsAdapter?.filterItems(searchEditText.text as CharSequence)
            filterButton.visibility = View.GONE
            show(suggestionsListView)
        }
    }

    private fun hideSearchSuggestions() {
        if (suggestionsListView.visibility == View.VISIBLE) {
            if (currentSearchPhrase != null) {
                filterButton.visibility = View.VISIBLE

                if (currentSearchPhrase.isNullOrEmpty()) {
                    imageButtonClose.visibility = View.GONE
                    imageButtonSpeech.visibility = View.VISIBLE
                } else {
                    imageButtonClose.visibility = View.VISIBLE
                    imageButtonSpeech.visibility = View.GONE
                }
                searchEditText.setText(currentSearchPhrase, TextView.BufferType.EDITABLE)
            } else {
                imageButtonBack.visibility = View.GONE
                filterButton.visibility = View.VISIBLE
            }

            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.clearFocus()
            hide(suggestionsListView)
        }
    }

    private fun setupAnimations() {
        showAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                suggestionsListView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })

        hideAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                suggestionsListView.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }


    private fun show(view: View) {
        view.startAnimation(showAnimation)
    }


    private fun hide(view: View) {
        view.startAnimation(hideAnimation)
    }

    private fun dpToPixels(dpValue: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue.toFloat(),
                context?.resources?.displayMetrics).toInt()
    }
}