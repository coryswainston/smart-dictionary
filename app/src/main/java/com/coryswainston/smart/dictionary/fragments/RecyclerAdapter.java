package com.coryswainston.smart.dictionary.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.R;

/**
 * RecyclerAdapter
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private String[] words;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public RecyclerAdapter(String[] words) {
        this.words = words;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tab_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(words[position]);

    }



    @Override
    public int getItemCount() {
        return words.length;
    }
}
