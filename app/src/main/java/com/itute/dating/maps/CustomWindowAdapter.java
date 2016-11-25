package com.itute.dating.maps;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.itute.dating.R;

/**
 * Created by buivu on 21/11/2016.
 */
public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    private String urlAvatar;

    public CustomWindowAdapter(Activity context, String urlAvatar) {
        this.context = context;
        this.urlAvatar = urlAvatar;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.custom_marker_info, null);

        TextView txtAddress = (TextView) view.findViewById(R.id.marker_address);
        ImageView imgAvatarMarker = (ImageView) view.findViewById(R.id.marker_avatar);
        //set daata
        txtAddress.setText(marker.getTitle());
//        Glide.with(context)
//                .load(marker.getSnippet())
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.drawable.avatar)
//                .into(imgAvatarMarker);
        return view;
    }
}
