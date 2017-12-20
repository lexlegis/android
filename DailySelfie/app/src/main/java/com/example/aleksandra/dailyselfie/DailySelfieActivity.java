package com.example.aleksandra.dailyselfie;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class DailySelfieActivity extends Activity {

    static final int REQUEST_TAKE_PHOTO = 1;

    public static final String SELFIE_PATH = "selfiePath";

    private DailySelfieAdapter selfieAdapter;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Selfie chosenSelfie = (Selfie)adapterView.getItemAtPosition(i);
                Intent chosenSelfieIntent = new Intent(getApplicationContext(), ChosenSelfieActivity.class);
                chosenSelfieIntent.putExtra(SELFIE_PATH, chosenSelfie.getPath());
                startActivity(chosenSelfieIntent);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selfieAdapter.toggleChecked(i);
                return true;
            }
        });


        selfieAdapter = new DailySelfieAdapter(getApplicationContext());
        selfieAdapter.addAll(loadSelfiesFromPath(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()));
        gridView.setAdapter(selfieAdapter);

        scheduleNotification(getNotification("Time for new selfie!"), 10000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = selfieAdapter.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera :
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            selfieAdapter.add(0, new Selfie(loadPic(mCurrentPhotoPath, true), new Date(), mCurrentPhotoPath));
        }
    }

    public static Bitmap loadPic(String mCurrentPhotoPath, boolean isScaled) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;

        if(isScaled) {
            // Get the dimensions of the View
            int targetW = 80;
            int targetH = 80;
            // Determine how much to scale down the image
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        return  bitmap;
    }

    private ArrayList<Selfie> loadSelfiesFromPath(String fileOrDirectoryPath) {
        ArrayList<Selfie> selfies = new ArrayList<>();
        File selfiesPath = new File(fileOrDirectoryPath);
        if(selfiesPath.isDirectory()) {
            File[] allSelfies = selfiesPath.listFiles();
            for(int i = allSelfies.length - 1; i >= 0; i--) {
                selfies.add(new Selfie(loadPic(allSelfies[i].getAbsolutePath(), true), new Date(), allSelfies[i].getAbsolutePath()));
            }
        }
        return selfies;
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Intent intent = new Intent(getApplicationContext(), DailySelfieActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("Daily Selfie Notification")
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder.build();
    }
}
