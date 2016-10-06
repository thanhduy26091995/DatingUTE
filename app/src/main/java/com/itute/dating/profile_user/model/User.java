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
    private String schoolName;
    private String facultyName;
    private String className;
    private boolean gender;
    private String phone;
    private long dateOfBirth;
    private boolean isAdd;
    private boolean isAdmin;
    private String photoURL;
    //default constructor

    public User() {
    }

    public User(String displayName, String photoURL, Map<String, Object> address, String phone, boolean isAdd, boolean isAdmin) {
        this.displayName = displayName;
        this.photoURL = photoURL;
        this.address = address;
        this.phone = phone;
        this.isAdd = isAdd;
        this.isAdmin = isAdmin;
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

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
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

    public boolean getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(boolean add) {
        isAdd = add;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
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
        myMap.put(Constants.SCHOOL_NAME, schoolName);
        myMap.put(Constants.FACULTY_NAME, facultyName);
        myMap.put(Constants.CLASS_NAME, className);
        myMap.put(Constants.GENDER, gender);
        myMap.put(Constants.PHONE, phone);
        myMap.put(Constants.DATE_OF_BIRTH, dateOfBirth);
        myMap.put(Constants.IS_ADD, isAdd);
        myMap.put(Constants.IS_ADMIN, isAdmin);
        myMap.put(Constants.PHOTO_URL, photoURL);
        return myMap;
    }
}
