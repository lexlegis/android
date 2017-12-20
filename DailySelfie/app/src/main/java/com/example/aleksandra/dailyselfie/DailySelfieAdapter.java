package com.example.aleksandra.dailyselfie;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aleksandra on 9.10.17..
 */

public class DailySelfieAdapter extends BaseAdapter {

    private ArrayList<Selfie> list = new ArrayList<>();
    private static LayoutInflater inflater = null;
    private Context mContext;

    public DailySelfieAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View newView = view;
        ViewHolder holder;

        Selfie selfie = list.get(i);

        if(view == null) {
            holder = new ViewHolder();
            newView = inflater.inflate(R.layout.activity_daily_selfie, null);
            holder.selfie = (ImageView) newView.findViewById(R.id.selfie);
            newView.setTag(holder);

        } else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.selfie.setImageBitmap(selfie.getBitmap());

        return newView;
    }

    static class ViewHolder {

        ImageView selfie;
    }

    public void add(Selfie selfie) {
        list.add(selfie);
        notifyDataSetChanged();
    }

    public void add(int index, Selfie selfie) {
        list.add(index, selfie);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Selfie> selfies) {
        list.addAll(selfies);
        notifyDataSetChanged();
    }

    public void toggleChecked(int i) {
        list.get(i).toggleChecked();
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }
}
