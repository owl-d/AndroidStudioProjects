package com.example.db_test;

import android.view.View;

public interface ListAdapterClickListener {
    public void onItemClick(ListAdapter.ViewHolder holder, View view, int position);
}