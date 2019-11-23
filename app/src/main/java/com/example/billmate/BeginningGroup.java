package com.example.billmate;

import java.util.ArrayList;

public class BeginningGroup {

    private String nameOfGroup;
    private ArrayList<String> members = new ArrayList<String>();

    public BeginningGroup() {
    }

    public String getNameOfGroup() {
        return nameOfGroup;
    }

    public void setNameOfGroup(String nameOfGroup) {
        this.nameOfGroup = nameOfGroup;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

}
