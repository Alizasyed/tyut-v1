package com.lumos.durshaltyut;

import java.io.Serializable;

public class Booking implements Serializable {
    public String name, contact, userid, grade, selected_topic, selected_subject, selected_location;


    public Booking() {
    }

    public Booking( String name, String contact, String grade, String selected_topic, String selected_subject, String selected_location) {
        this.userid = userid;
        this.name = name;
        this.contact = contact;
        this.grade = grade;
        this.selected_location = selected_location;
        this.selected_subject = selected_subject;
        this.selected_topic = selected_topic;
    }


}

