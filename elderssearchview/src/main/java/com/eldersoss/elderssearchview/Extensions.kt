package com.eldersoss.elderssearchview

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.resolveActivity(): Activity {
    var context: Context = this
    while (context as? Activity == null) {
        context = (context as ContextWrapper).baseContext
    }
    return context
}