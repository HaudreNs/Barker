package com.nbu.barker;

public class User {
    private String m_sUsername;
    private String m_sEmail;
    private String m_sNames;
    private int m_nId;
    private String m_sPassword;
    private boolean m_bIsFriendAccepted;

    User(){}
    User(String sEmail, String sUsername, String sNames, boolean bIsFriendAccepted) {
        m_sEmail = sEmail;
        m_sUsername = sUsername;
        String m_sNames = sNames;
        m_bIsFriendAccepted = bIsFriendAccepted;

    }


    public String getUsername()
    {
        return m_sUsername;
    }
    public void setUsername(String sUsername)
    {
        m_sUsername = sUsername;
    }

    public String getEmail() {
        return m_sEmail;
    }

    public String getNames() {
        return m_sNames;
    }

    public void setEmail(String sEmail) {
        m_sEmail = sEmail;
    }

    public void setNames(String sNames) {
        m_sNames = sNames;
    }

    public void setId(int nId) {m_nId = nId;}
    public int getId() {return m_nId;}

    public String getPassword() {
        return m_sPassword;
    }

    public void setPassword(String sPassword) {
        m_sPassword = sPassword;
    }

    public boolean getIsFriendAccepted() {return m_bIsFriendAccepted;}
    public void setIsFriendAccepted(boolean bAccepted) {m_bIsFriendAccepted = bAccepted;}
}
