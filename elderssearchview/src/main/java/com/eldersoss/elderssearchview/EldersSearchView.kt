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
import android.view.animation.ScaleAnimation
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
    private var searchHintText: String = ""
    private var iconsColor = 7565683
    private var iconsWidth = dpToPixels(48)
    private var suggestionsFileName = "SuggestionsFile"
    private var searchBarHeight = dpToPixels(48)


    private var searchSuggestionsAdapter: SearchSuggestionsAdapter? = null

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

    private val hideAnimation = ScaleAnimation(
            1f, 1f,
            1f, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f)

    private val showAnimation = ScaleAnimation(
            1f, 1f,
            0f, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f)

    private var currentSearchWord: String? = null

    private val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val customAttributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.EldersSearchView,
                0, 0)

        for (i in 0 until customAttributes.indexCount) {
            val a = customAttributes.getIndex(i)
            when (a) {
                R.styleable.EldersSearchView_hintText -> {
                    searchHintText = customAttributes.getString(a)
                }

                R.styleable.EldersSearchView_iconsColor -> {
                    iconsColor = customAttributes.getColor(a, iconsColor)
                }

                R.styleable.EldersSearchView_suggestionsFileName -> {
                    suggestionsFileName = customAttributes.getString(a)
                }

                R.styleable.EldersSearchView_searchViewHeight -> {
                    searchBarHeight = customAttributes.getDimensionPixelSize(a, searchBarHeight)
                }
                R.styleable.EldersSearchView_iconsWidth -> {
                    iconsWidth = customAttributes.getDimensionPixelSize(a, iconsWidth)
                }
            }
        }

        customAttributes.recycle()

        searchSuggestionsAdapter = SearchSuggestionsAdapter((context as Activity), { searchForText(it) }, suggestionsFileName, iconsColor, iconsWidth)
        suggestionsListView.adapter = searchSuggestionsAdapter

        applyStyles()
        initViewState()
        initListeners()

        setupAnimations()
    }

    fun setOnSearchListener(listener: ((word: String) -> Unit)?) {
        searchListener = listener
    }

    fun setOnBackListener(listener: (() -> String?)?) {
        backListener = listener
    }

    override fun onTextResult(text: String) {
        if (text.isNotBlank()) {
            imageButtonSearch.visibility = View.GONE
            searchHint.visibility = View.GONE
            imageButtonSpeech.visibility = View.GONE
            searchEditText.setText(text, TextView.BufferType.EDITABLE)
            searchForText(text)
        }
    }

    private fun initViewState() {
        filterButton.visibility = View.GONE
        imageButtonClose.visibility = View.GONE
        imageButtonBack.visibility = View.GONE
        searchEditText.clearFocus()
    }

    private fun applyStyles() {
        // Build views tree
        linearLayoutLeft.id = View.generateViewId()
        linearLayoutLeft.addView(imageButtonBack)
        linearLayoutLeft.addView(imageButtonSearch)

        linearLayoutRight.id = View.generateViewId()
        linearLayoutRight.addView(imageButtonClose)
        linearLayoutRight.addView(imageButtonSpeech)
        linearLayoutRight.addView(filterButton)

        this.addView(linearLayoutLeft)

        this.addView(searchEditText)
        this.addView(searchHint)

        this.addView(linearLayoutRight)
        this.addView(suggestionsListView)

        val imageButtonsParams = LinearLayout.LayoutParams(
                iconsWidth,
                searchBarHeight
        )

        // style left side
        linearLayoutLeft.orientation = LinearLayout.HORIZONTAL
        imageButtonBack.layoutParams = imageButtonsParams
        imageButtonBack.scaleType = ImageView.ScaleType.CENTER
        imageButtonBack.setImageResource(R.drawable.material_icon_arrow_left)
        imageButtonBack.setColorFilter(iconsColor)

        imageButtonSearch.layoutParams = imageButtonsParams
        imageButtonSearch.scaleType = ImageView.ScaleType.CENTER
        imageButtonSearch.setImageResource(R.drawable.material_icon_magnify)
        imageButtonSearch.setColorFilter(iconsColor)

        // style input field
        searchHint.text = searchHintText
        searchHint.textSize = 18F
        searchHint.ellipsize = TextUtils.TruncateAt.END
        searchHint.maxLines = 1
        val searchHintParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                searchBarHeight
        )
        searchHintParams.addRule(RelativeLayout.RIGHT_OF, linearLayoutLeft.id)
        searchHint.layoutParams = searchHintParams
        searchHint.gravity = Gravity.CENTER_VERTICAL
        searchHint.setTextColor(iconsColor)


        val searchEditTextParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                searchBarHeight
        )
        searchEditTextParams.addRule(RelativeLayout.RIGHT_OF, linearLayoutLeft.id)
        searchEditTextParams.addRule(RelativeLayout.LEFT_OF, linearLayoutRight.id)
        searchEditTextParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        searchEditText.layoutParams = searchEditTextParams
        searchEditText.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchEditText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        searchEditText.isLongClickable = false
        searchEditText.maxLines = 1
        searchEditText.setSingleLine(true)
        searchEditText.setTextIsSelectable(false)
        searchEditText.setBackgroundColor(0)
        searchEditText.gravity = Gravity.CENTER_VERTICAL //TODO: need to be fixed for different resolutions

        // style right side
        (linearLayoutRight.layoutParams as LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

        imageButtonClose.layoutParams = imageButtonsParams
        imageButtonClose.scaleType = ImageView.ScaleType.CENTER
        imageButtonClose.setImageResource(R.drawable.material_icon_close)
        imageButtonClose.setColorFilter(iconsColor)

        imageButtonSpeech.layoutParams = imageButtonsParams
        imageButtonSpeech.scaleType = ImageView.ScaleType.CENTER
        imageButtonSpeech.setImageResource(R.drawable.material_icon_microphone)
        imageButtonSpeech.setColorFilter(iconsColor)

        filterButton.layoutParams = imageButtonsParams
        filterButton.scaleType = ImageView.ScaleType.CENTER
        filterButton.setImageResource(R.drawable.material_icon_filter)
        filterButton.setColorFilter(iconsColor)


        val suggestionsListViewLayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        suggestionsListViewLayoutParams.setMargins(
                0,
                searchBarHeight,
                0,
                0
        )
        suggestionsListView.setBackgroundResource(R.drawable.stroke_at_top)
        suggestionsListView.divider = null
        suggestionsListView.layoutParams = suggestionsListViewLayoutParams
        suggestionsListView.visibility = View.GONE

    }

    private fun initListeners() {
        imageButtonSpeech.setOnClickListener {
            try {
                SpeechSearchDialog(context as Activity, this)
            } catch (e: ClassCastException) {
                Log.e(this.javaClass.name, "")
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

        imageButtonBack.setOnClickListener({
            if (suggestionsListView.visibility == View.VISIBLE) {
                hideSearchSuggestions()
                if (currentSearchWord == null) {
                    closeSearching()
                } else {

                }
                return@setOnClickListener
            }
            val w = backListener?.invoke()
            if (w == null) {
                currentSearchWord = null
                closeSearching()
            } else {
                currentSearchWord = w
                searchEditText.setText(w, TextView.BufferType.EDITABLE)
                filterButton.visibility = View.VISIBLE
            }
        })
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
        searchEditText.setText(text, TextView.BufferType.EDITABLE)
        filterButton.visibility = View.VISIBLE
        currentSearchWord = searchText
        hideSearchSuggestions()
        searchListener?.invoke(text)
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
            if (currentSearchWord != null) {
                filterButton.visibility = View.VISIBLE

                if (currentSearchWord.isNullOrEmpty()) {
                    imageButtonClose.visibility = View.GONE
                    imageButtonSpeech.visibility = View.VISIBLE
                } else {
                    imageButtonClose.visibility = View.VISIBLE
                    imageButtonSpeech.visibility = View.GONE
                }
                searchEditText.setText(currentSearchWord, TextView.BufferType.EDITABLE)
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
        hideAnimation.duration = 300
        hideAnimation.fillAfter = true
        hideAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                suggestionsListView.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        showAnimation.duration = 300
        showAnimation.fillAfter = true

    }

    private fun show(view: View) {
        view.visibility = View.VISIBLE
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