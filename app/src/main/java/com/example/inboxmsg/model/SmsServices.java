package com.example.inboxmsg.model;

import java.util.List;

public class SmsServices {

    private  String senderInfo;
    private List<String> senderBodyList;


    public SmsServices(String senderInfo, List<String> senderBodyList) {
        this.senderInfo = senderInfo;
        this.senderBodyList = senderBodyList;
    }

    public String getSenderInfo() {
        return senderInfo;
    }

    public List<String> getSenderBodyList() {
        return senderBodyList;
    }

    @Override
    public String toString() {
        return "SmsServices{" +
                "senderInfo='" + senderInfo + '\'' +
                ", senderBodyList=" + senderBodyList +
                '}';
    }

}
