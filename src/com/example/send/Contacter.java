package com.example.send;


/**
 * Created by root on 15-9-14.
 */
public class Contacter {
    private String phoneNumber;
    private String userName;
    private String sortKey;
    private String contactId;

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getSortKey() {
        return sortKey;
    }

    public String getContactId() {
        return contactId;
    }
}

