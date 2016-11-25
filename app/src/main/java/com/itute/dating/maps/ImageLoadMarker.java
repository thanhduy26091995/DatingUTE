package com.itute.dating.maps;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by buivu on 21/11/2016.
 */
public class ImageLoadMarker extends AsyncTask<Void, Void, Void> {
    private String avatarUrl;
    private GoogleMap googleMap;

    private Activity mActivity;
    private Marker marker;

    public ImageLoadMarker(Activity mActivity, GoogleMap googleMap, Marker marker, String avatarUrl) {
        this.mActivity = mActivity;
        this.googleMap = googleMap;
        this.marker = marker;
        this.avatarUrl = avatarUrl;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
