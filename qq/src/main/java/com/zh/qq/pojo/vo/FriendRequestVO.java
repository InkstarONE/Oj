package com.zh.qq.pojo.vo;

import javax.persistence.Column;
import javax.persistence.Id;



//好友请求发送方的信息
public class FriendRequestVO {
    private String sendUserId;

    /**
     * 用户名，账号，
     */
    private String sendUsername;

    /**
     * 密码
     */

    /**
     * 我的头像，如果没有默认给一张
     */
    private String sendFaceImage;

    private String sendNickname;


    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getSendUsername() {
        return sendUsername;
    }

    public void setSendUsername(String sendUsername) {
        this.sendUsername = sendUsername;
    }

    public String getSendFaceImage() {
        return sendFaceImage;
    }

    public void setSendFaceImage(String sendFaceImage) {
        this.sendFaceImage = sendFaceImage;
    }

    public String getSendNickname() {
        return sendNickname;
    }

    public void setSendNickname(String sendNickname) {
        this.sendNickname = sendNickname;
    }
}