package com.example.billmate.itemsBean;

import java.util.ArrayList;

public class BeginningGroup {

    private String nameOfGroup;
    private String idDocFirebase;
    private ArrayList<String> members = new ArrayList<String>();

    public BeginningGroup() {
    }

    public String getNameOfGroup() {
        return nameOfGroup;
    }

    public void setNameOfGroup(String nameOfGroup) {
        this.nameOfGroup = nameOfGroup;
    }

    public String getIdDocFirebase() {
        return idDocFirebase;
    }

    public void setIdDocFirebase(String idDocFirebase) {
        this.idDocFirebase = idDocFirebase;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public void addElem(String member) {
        this.members.add(member);
    }

    public void removeElem(int position) {
        this.members.remove(position);
    }

    public int getSize() {
        return members.size();
    }
}
