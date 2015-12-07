package com.example.send;


import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 15-9-14.
 */
public class Contacter {
    private String displayName;
    private String sortKey;
    private String contactId;
    private String lookupKey;
    private Spanned spanName;
    private Spanned spanNum;
    private int isDeleted;
    private int version;
    private List<String> phoneNumberList = null;
    Contacter(){
        phoneNumberList = new ArrayList<>();
    }

    public void setAll(Contacter contacter){
        displayName = contacter.displayName;
        sortKey = contacter.sortKey;
        contactId = contacter.contactId;
        lookupKey = contacter.lookupKey;
        isDeleted = contacter.isDeleted;
        version = contacter.version;
        spanName = contacter.spanName;
        spanNum = contacter.spanNum;
        phoneNumberList.clear();
        phoneNumberList.addAll(contacter.getPhoneNumberList());
    }
    public void addPhoneNumber(String phoneNumber) {
        phoneNumberList.add(phoneNumber);
    }

    public void addPhoneNumberList(List<String> list){
        phoneNumberList.addAll(list);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setSpanName(Spanned spanName) {
        this.spanName = spanName;
    }

    public void setSpanNum(Spanned spanNum) {
        this.spanNum = spanNum;
    }

    public String getPhoneNumber(int i) {
        if (i < phoneNumberList.size())
            return phoneNumberList.get(i);
        else
            return null;
    }
    public List<String> getPhoneNumberList(){
        return phoneNumberList;
    }

    public int getPhoneNumberSize(){
        return phoneNumberList.size();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSortKey() {
        return sortKey;
    }

    public String getContactId() {
        return contactId;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public int getVersion() {
        return version;
    }

    public int getIsDeleted(){
        return isDeleted;
    }

    public Spanned getSpanName() {
        return spanName;
    }

    public Spanned getSpanNum() {
        return spanNum;
    }
}

