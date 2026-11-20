package com.mirea.kt.ribo.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EntryAdapter extends ArrayAdapter<Entry> {

    public EntryAdapter(Context context, List<Entry> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_entry, parent, false);
        }

        Entry entry = getItem(position);
        TextView tvService = convertView.findViewById(R.id.tvService);
        TextView tvLogin = convertView.findViewById(R.id.tvLogin);

        tvService.setText(entry.service);
        tvLogin.setText(entry.login);

        return convertView;
    }
}
