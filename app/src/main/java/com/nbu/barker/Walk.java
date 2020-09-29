package com.nbu.barker;

public class Walk {
    private String m_sMessage;
    private String m_sUsername;
    private int m_nTimeAgo;
    private int m_nId;

    public Walk(String sMessage, String sUsername, int nTimeAgo, int nId) {
        m_sMessage = sMessage;
        m_sUsername = sUsername;
        m_nTimeAgo = nTimeAgo;
        m_nId = nId;
    }

    public Walk(){}


    public int getId() {
        return m_nId;
    }

    public void setId(int nId) {
        m_nId = nId;
    }



    public String getMessage() {
        return m_sMessage;
    }

    public void setMessage(String sMessage) {
        m_sMessage = sMessage;
    }

    public int getTimePassed() {
        return m_nTimeAgo;
    }

    public void setTimePassed(int nTimeAgo) {
        this.m_nTimeAgo = nTimeAgo;
    }

    public String getUsername() {
        return m_sUsername;
    }

    public void setUsername(String sUsername) {
        m_sUsername = sUsername;
    }
}
