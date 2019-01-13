package com.groupchat.jasonchesney.groupchat;

public class GroupModel {

    String grouptitle, groupimage;

    public GroupModel() {}

    public GroupModel(String grouptitle, String groupimage) {
        this.grouptitle = grouptitle;
        this.groupimage = groupimage;
    }

    public String getGrouptitle() {
        return grouptitle;
    }

    public void setGrouptitle(String grouptitle) {
        this.grouptitle = grouptitle;
    }

    public String getGroupimage() {
        return groupimage;
    }

    public void setGroupimage(String groupimage) {
        this.groupimage = groupimage;
    }
}
