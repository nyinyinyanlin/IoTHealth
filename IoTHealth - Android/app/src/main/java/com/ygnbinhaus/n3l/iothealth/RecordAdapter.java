package com.ygnbinhaus.n3l.iothealth;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class RecordAdapter extends ArrayAdapter<Record>{

    public RecordAdapter(Context context,ArrayList<Record> records) {
        super(context, 0, records);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Record record = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_record_listview_item, parent, false);
        }

        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvHeartrate = (TextView) convertView.findViewById(R.id.tvHeartrate);
        TextView tvOxygen = (TextView) convertView.findViewById(R.id.tvOxygen);
        TextView tvTemperature = (TextView) convertView.findViewById(R.id.tvTemperature);
        TextView tvComment = (TextView) convertView.findViewById(R.id.tvComment);
        tvDate.setText(record.getDate());
        tvHeartrate.setText(record.getHeartrate().toString()+"BPM");
        tvOxygen.setText(record.getOxygen().toString()+"%");
        tvTemperature.setText(record.getTemperature().toString()+"'F");
        if(!record.getComment().equals("")) {
            tvComment.setVisibility(View.VISIBLE);
            tvComment.setText(record.getComment());
        }
        return convertView;
    }
}
