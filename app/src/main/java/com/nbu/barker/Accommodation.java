package com.nbu.barker;

public class Accommodation {

    private int m_nId;
    private String m_sName;
    private String m_sDescription;
    private String m_sUsername;
    private double m_nRating;
    private int m_nVoted;

    Accommodation(int nId, String sName, String sDescription, String sUsername, double nRating, int nVoted)
    {
        m_nId = nId;
        m_sName = sName;
        m_sDescription = sDescription;
        m_sUsername = sUsername;
        m_nRating = nRating;
        m_nVoted = nVoted;
    }


    public void setM_nId(int m_nId) {
        this.m_nId = m_nId;
    }

    public void setName(String m_sName) {
        this.m_sName = m_sName;
    }

    public void setDescription(String m_sDescription) {
        this.m_sDescription = m_sDescription;
    }

    public void setUsername(String m_sUsername) {
        this.m_sUsername = m_sUsername;
    }

    public void setRating(double m_nRating) {
        this.m_nRating = m_nRating;
    }

    public void setVoted(int m_nVoted) {
        this.m_nVoted = m_nVoted;
    }

    public int getId() {
        return m_nId;
    }

    public String getName() {
        return m_sName;
    }

    public String getDescription() {
        return m_sDescription;
    }

    public String getUsername() {
        return m_sUsername;
    }

    public double getRating() {
        return m_nRating;
    }

    public int getVoted() {
        return m_nVoted;
    }

}
