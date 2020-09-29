package com.nbu.barker;

public class Comment {
    private String m_sUserUsername;
    private String m_sComment;
    private int m_nSubjectId;
    private int m_nCommentId;

    Comment(int nId, String sUserUsername, String sComment)
    {
        m_nCommentId = nId;
        m_sUserUsername = sUserUsername;
        m_sComment = sComment;
    }


    public String getUserUsername() {
        return m_sUserUsername;
    }

    public void setUserUsername(String sUserUsername) {
        m_sUserUsername = sUserUsername;
    }

    public int getCommentId() {
        return m_nCommentId;
    }

    public void setCommentId(int nCommentId) {
        m_nCommentId = nCommentId;
    }

    public int getSubjectId() {
        return m_nSubjectId;
    }

    public void setSubjectId(int nSubjectId) {
        m_nSubjectId = nSubjectId;
    }

    public String getComment() {
        return m_sComment;
    }

    public void setComment(String sComment) {
        m_sComment = sComment;
    }
}
