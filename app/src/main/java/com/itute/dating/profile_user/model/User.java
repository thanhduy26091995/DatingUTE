package com.itute.dating.profile_user.model;

import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 06/10/2016.
 */
public class User {
    private String displayName;
    private Map<String, Object> address;
    private int gender;
    private String phone;
    private long dateOfBirth;
    private String photoURL;
    private String hobby;
    private String star;
    private String job;
    private String language;
    private String religion;
    private long timestamp;
    private int old;
    private boolean isLogin;
    private Map<String, Boolean> hearts = new HashMap<>();
    private int heartCount;
    //default constructor

    public User() {
    }

    public User(String displayName, String photoURL, Map<String, Object> address, String phone, int gender, long timestamp,
                String hobby, String star, String job, String language, String religion) {
        this.displayName = displayName;
        this.photoURL = photoURL;
        this.address = address;
        this.phone = phone;
        this.gender = gender;
        this.timestamp = timestamp;
        this.hobby = hobby;
        this.star = star;
        this.job = job;
        this.language = language;
        this.religion = religion;
    }

    public int getHeartCount() {
        return heartCount;
    }

    public void setHeartCount(int heartCount) {
        this.heartCount = heartCount;
    }

    public Map<String, Boolean> getHearts() {
        return hearts;
    }

    public void setHearts(Map<String, Boolean> hearts) {
        this.hearts = hearts;
    }

    public boolean getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean login) {
        isLogin = login;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public int getOld() {
        return old;
    }

    public void setOld(int old) {
        this.old = old;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, Object> getAddress() {
        return address;
    }

    public void setAddress(Map<String, Object> address) {
        this.address = address;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.DISPLAY_NAME, displayName);
        myMap.put(Constants.ADDRESS, address);
        myMap.put(Constants.GENDER, gender);
        myMap.put(Constants.PHONE, phone);
        myMap.put(Constants.DATE_OF_BIRTH, dateOfBirth);
        myMap.put(Constants.PHOTO_URL, photoURL);
        myMap.put(Constants.TIMESTAMP, timestamp);
        myMap.put(Constants.HOBBY, hobby);
        myMap.put(Constants.JOB, job);
        myMap.put(Constants.LANGUAGE, language);
        myMap.put(Constants.STAR, star);
        myMap.put(Constants.RELIGION, religion);
        myMap.put(Constants.OLD, old);
        myMap.put(Constants.HEART_COUNT, heartCount);
        return myMap;
    }
}
