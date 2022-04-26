package com.phms.pojo;

import java.util.Date;
import java.util.List;

public class TbComment{
    private Integer id;

    private String username;

    private String replyTo;

    private String content;

    private Integer parentId;
    private String createUserId;

    private Date createTime;
    private List<TbComment> commentList;

    public List<TbComment> getCommentList() {
        return commentList;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public void setCommentList(List<TbComment> commentList) {
        this.commentList = commentList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo == null ? null : replyTo.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}