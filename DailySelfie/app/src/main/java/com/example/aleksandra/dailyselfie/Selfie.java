package com.example.aleksandra.dailyselfie;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by aleksandra on 9.10.17..
 */

public class Selfie {

    private Bitmap bitmap;
    private Date createdDate;
    private boolean isChecked = false;

    private String path;

    public  Selfie(Bitmap bitmap, Date createdDate, String path) {
        this.bitmap = bitmap;
        this.createdDate = createdDate;
        this.path = path;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public boolean isChecked(){
        return isChecked;
    }
    public void toggleChecked() {
        isChecked = !isChecked;
    }
}
