package com.nbu.barker;

import java.util.Vector;

public class Subject {

    private String m_sName;
    private String m_sDescription;
    private String m_sUserUsername;
    private int m_nId;
    private int m_nInternalId;
    private Vector<Comment> m_vpComments;

    Subject(int m_nId,String m_sName, String m_sUserUsername)
    {
        this.m_nId = m_nId;
        this.m_sName = m_sName;
        this.m_sUserUsername = m_sUserUsername;
    }


    public int getId() {
        return m_nId;
    }

    public void setId(int nId) {
        m_nId = nId;
    }

    public String getDescription() {
        return m_sDescription;
    }

    public void setDescription(String sDescription) {
        m_sDescription = sDescription;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String sName) {
        m_sName = sName;
    }

    public String getUserUsername() {
        return m_sUserUsername;
    }

    public void setUserUsername(String sUserUsername) {
        m_sUserUsername = sUserUsername;
    }

    public Vector<Comment> getComments() {
        return m_vpComments;
    }

    public void setComments(Vector<Comment> vpComments) {
        m_vpComments = vpComments;
    }

    public int getInternalId() {
        return m_nInternalId;
    }

    public void setInternalId(int nInternalId) {
        m_nInternalId = nInternalId;
    }
}
