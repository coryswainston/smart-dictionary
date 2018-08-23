package com.coryswainston.smart.dictionary.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerAdapter
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<String> words;
    private int selectedIndex;

    private Drawable underline;
    private int blue;
    private int white;
    private int grey;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public RecyclerAdapter(Context context, List<String> words) {
        this.words = words;
        Resources res = context.getResources();
        underline = res.getDrawable(R.drawable.underline);
        blue = res.getColor(R.color.colorPrimary);
        white = Color.WHITE;
        grey = res.getColor(R.color.colorDeselected);
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tab_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        // recycling is causing me grief and there aren't going to be that many items
        vh.setIsRecyclable(false);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("RecyclerAdapter", "called with position: " + position);
        holder.textView.setText(words.get(position));
        if (position == selectedIndex) {
            holder.textView.setBackground(underline);
            holder.textView.setTextColor(blue);
        } else {
            holder.textView.setBackgroundColor(white);
            holder.textView.setTextColor(grey);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}
