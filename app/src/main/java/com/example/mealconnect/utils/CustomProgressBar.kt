package com.example.mealconnect.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mealconnect.R

class CustomProgressBar(context: Context, attrs: AttributeSet? = null) :RelativeLayout(context, attrs) {

    private val progressBar: ProgressBar
    private val textView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_progress_bar, this, true)
        progressBar = findViewById(R.id.customprogress)
        textView = findViewById(R.id.txtviewprogressbar)
        hide()
    }

    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = GONE
    }

    fun setText(text: String) {
        textView.text = text
    }
}
