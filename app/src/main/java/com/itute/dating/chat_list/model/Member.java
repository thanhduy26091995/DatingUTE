package com.itute.dating.chat_list.model;

import java.util.ArrayList;

/**
 * Created by buivu on 05/12/2016.
 */
public class Member {
    private ArrayList<String> member;

    public Member() {
    }

    public Member(ArrayList<String> member) {
        this.member = member;
    }

    public ArrayList<String> getMember() {
        return member;
    }

    public void setMember(ArrayList<String> member) {
        this.member = member;
    }
}
