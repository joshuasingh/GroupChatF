package com.groupchat.jasonchesney.groupchat;

public class MemberModel {

    String pname, pimage;

    public MemberModel() {}

    public MemberModel(String pname, String pimage) {
        this.pname = pname;
        this.pimage = pimage;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }
}
