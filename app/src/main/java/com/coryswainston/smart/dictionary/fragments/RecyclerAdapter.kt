package com.coryswainston.smart.dictionary.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.coryswainston.smart.dictionary.R

/**
 * RecyclerAdapter
 */
class RecyclerAdapter(context: Context, private val words: List<String>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    var selectedIndex = 0

    private val underline: Drawable
    private val blue: Int
    private val white: Int
    private val grey: Int

    class ViewHolder(var textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.tab_view, parent, false) as TextView
        val vh = ViewHolder(v)
        // recycling is causing me grief and there aren't going to be that many items
        vh.setIsRecyclable(false)
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "called with position: $position")
        holder.textView.text = words[position]
        if (position == selectedIndex) {
            holder.textView.background = underline
            holder.textView.setTextColor(blue)
        } else {
            holder.textView.setBackgroundColor(white)
            holder.textView.setTextColor(grey)
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    init {
        val res = context.resources
        @Suppress("deprecation")
        underline = res.getDrawable(R.drawable.underline)
        @Suppress("deprecation")
        blue = res.getColor(R.color.colorPrimary)
        white = Color.WHITE
        @Suppress("deprecation")
        grey = res.getColor(R.color.colorDeselected)
    }
}