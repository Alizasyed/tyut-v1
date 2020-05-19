package com.lumos.durshaltyut;

public class userinfo{

    public String name;
    public String email;
    public String contact;
    public String userid;

    public userinfo(){}

    public userinfo(String userid, String name, String email, String contact) {
        this.userid = userid;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }
    public userinfo(String userid, String email, String name) {
        this.userid = userid;
        this.name = name;
        this.email = email;
    }

    public userinfo(String name, String email) {
        this.name = name;

        this.email = email;
    }


}
