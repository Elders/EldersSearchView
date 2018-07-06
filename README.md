# EldersSearchView
Custom View Search Bar with suggestions

[![screen](https://raw.githubusercontent.com/Elders/EldersSearchView/master/media/esvdemo1.gif)](https://github.com/Elders/EldersSearchView)

Download
--------
[ ![Download](https://api.bintray.com/packages/r2d2/EldersSearchView/EldersSearchView/images/download.svg) ](https://bintray.com/r2d2/EldersSearchView/EldersSearchView/_latestVersion)
```groovy
dependencies {
    implementation 'com.eldersoss:elderssearchview:1.0.0'
}
```

Usage
--------
Simplest way:
```xml
    <com.eldersoss.elderssearchview.EldersSearchView
        android:id="@+id/elders_search_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:esvHintText="Search for something" />
```
Custom attributes
--------
```text
esvHintText - (string) hint text for the search bar
esvHintTextColor - (color) color of the hint text
esvIconsColor - (color) color of the icons
esvSuggestionsFileName - (string) file name that searched phrases will be saved
esvSearchViewHeight - (dimension) Height of the search bar
esvIconsWidth - (dimension) width of all icons in the search bar and the suggestions fragment
esvElevation - (dimension) search bar elevation (supported in API Level 21+)
esvMargin - (dimension) search bar margins
esvBackground - (reference, drawable resourceId) it overrides default search bar background
esvSuggestionsBackground - (reference, drawable resourceId) it overrides default suggestion dialog background
esvSpeechRecognizerLogo - (reference, drawable resourceId) a little logo in bottom of Speech Recognizer popup dialog.
esvAlwaysBack - (boolean - false)
esvAlwaysFilter - (boolean - false)
esvNoFilter - (boolean - true)
esvSuggestionsEnabled - (boolean - true), enable/disable suggestion
```
Public methods
--------
```kotlin
esv.setOnSearchListener { phrase -> } // function wit one parameter String
esv.setOnSearchTextChangeListener { chars -> } // function wit one parameter CharSequence optional
esv.setOnBackListener { "last phrase from backstack" } // function which returns phrase from the searches history
esv.clickBackButton() // it returns Boolean true if something changed (eg: suggestions has been hidden)
esv.setSearchedPhrase(phrase: String) // directly set text in the search view
esv.searchForPhrase(phrase: String) // execute search logicc with given phrase
esv.clearSearch() // it clears text from the search bar
```
