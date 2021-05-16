package com.hemanthkaipa.madgames.play

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hemanthkaipa.madgames.R

class StyleTile(private val context : Context) {

    fun styleTextView(obj: Any, value: Int) {
        val textView = obj as TextView
        textView.text = if (value <= 0) "" else "$value"
        var textColor = ContextCompat.getColor(context, R.color.default_tile_text_color)
        var tileBackgroundColor = ContextCompat.getColor(context, R.color.default_tile_color)
        when (value) {
            2 -> tileBackgroundColor = ContextCompat.getColor(context, R.color.color_2)
            4 -> tileBackgroundColor = ContextCompat.getColor(context, R.color.color_4)
            8 -> {
                textColor = ContextCompat.getColor(context, R.color.default_tile_text_color)
                tileBackgroundColor = ContextCompat.getColor(context, R.color.color_8)
            }
            16 -> {
                textColor = ContextCompat.getColor(context, R.color.default_tile_text_color)
                tileBackgroundColor = ContextCompat.getColor(context, R.color.color_16)
            }
            32 -> {
                textColor = ContextCompat.getColor(context, R.color.default_tile_text_color)
                tileBackgroundColor = ContextCompat.getColor(context, R.color.color_32)
            }
            64 -> {
                textColor = ContextCompat.getColor(context, R.color.default_tile_text_color)
                tileBackgroundColor = ContextCompat.getColor(context, R.color.color_64)
            }
            128 -> {
                textColor = ContextCompat.getColor(context, R.color.default_tile_text_color)
                tileBackgroundColor = ContextCompat.getColor(context, R.color.color_128)
            }
            256 -> tileBackgroundColor = ContextCompat.getColor(context, R.color.color_256)
            512 -> tileBackgroundColor = ContextCompat.getColor(context, R.color.color_512)
            1024 -> tileBackgroundColor = ContextCompat.getColor(context, R.color.color_1024)
            2048 -> tileBackgroundColor = ContextCompat.getColor(context, R.color.color_2048)
        }
        textView.setBackgroundColor(tileBackgroundColor)
        textView.setTextColor(textColor)
    }
}