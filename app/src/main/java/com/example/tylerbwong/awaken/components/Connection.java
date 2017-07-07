package com.example.tylerbwong.awaken.components;

/**
 * @author Tyler Wong
 */
public class Connection {
    private int mId;
    private String mNickname;
    private String mHost;
    private String mMac;
    private String mPortWol;
    private String mPortDev;
    private String mCity;
    private String mState;
    private String mCountry;
    private String mStatus;
    private String mDate;

    public Connection(int id, String nickname, String host, String mac, String portWol, String portDev,
                      String city, String state, String country, String status, String date) {
        this.mId = id;
        this.mNickname = nickname;
        this.mHost = host;
        this.mMac = mac;
        this.mPortWol = portWol;
        this.mPortDev = portDev;
        this.mCity = city;
        this.mState = state;
        this.mCountry = country;
        this.mStatus = status;
        this.mDate = date;
    }

    public int getId() {
        return mId;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getHost() {
        return mHost;
    }

    public String getMac() {
        return mMac;
    }

    public String getmPortWol() {
        return mPortWol;
    }

    public String getmPortDev() {
        return mPortDev;
    }

    public String getCity() {
        return mCity;
    }

    public String getState() {
        return mState;
    }

    public String getmCountry() {
        return mCountry;
    }

    public String getmStatus() {
        return mStatus;
    }

    public String getDate() {
        return mDate;
    }
}
