package com.itute.dating.add_image.model;

import com.itute.dating.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 09/11/2016.
 */
public class Photo {
    private ArrayList<String> photoLink;

    public Photo(ArrayList<String> photoLink) {
        this.photoLink = photoLink;
    }

    public Photo() {
    }

    public ArrayList<String> getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(ArrayList<String> photoLink) {
        this.photoLink = photoLink;
    }
}
