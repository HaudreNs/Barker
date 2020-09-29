package com.nbu.barker;

import java.util.Vector;

public class SessionParameters {

    private static User m_pApplicationUser = null;
    private static int m_nTopicId= 0;

    private static Vector<Subject> m_vpSubjects;

    public static Vector<Subject> getSubjects() {
        return m_vpSubjects;
    }

    public static void setSubjects(Vector<Subject> vpSubjects) {
        m_vpSubjects = vpSubjects;
    }

    public static void setChosenTopic(int nInternalId) {
        m_nTopicId = nInternalId;
    }
    public static int getChosenTopic()
    {
        return m_nTopicId;
    }

    public static User getApplicationUser()
    {
        return m_pApplicationUser;
    }

    public static void setApplicationUser(User pApplicationUser)
    {
        m_pApplicationUser = pApplicationUser;
    }


}
