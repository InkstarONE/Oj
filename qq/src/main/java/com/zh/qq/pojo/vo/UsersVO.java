package com.zh.qq.pojo.vo;

import javax.persistence.Column;
import javax.persistence.Id;

public class UsersVO {
    @Id
    private String id;

    /**
     * 用户名，账号，
     */
    private String username;

    /**
     * 密码
     */

    /**
     * 我的头像，如果没有默认给一张
     */
    @Column(name = "face_image")
    private String faceImage;

    @Column(name = "face_image_big")
    private String faceImageBig;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 新用户注册后默认后台生成二维码，并且上传到fastdfs
     */
    private String qrcode;


    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户名，账号，慕信号
     *
     * @return username - 用户名，账号，慕信号
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名，账号，慕信号
     *
     * @param username 用户名，账号，慕信号
     */
    public void setUsername(String username) {
        this.username = username;
    }



    /**
     * 获取我的头像，如果没有默认给一张
     *
     * @return face_image - 我的头像，如果没有默认给一张
     */
    public String getFaceImage() {
        return faceImage;
    }

    /**
     * 设置我的头像，如果没有默认给一张
     *
     * @param faceImage 我的头像，如果没有默认给一张
     */
    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    /**
     * @return face_image_big
     */
    public String getFaceImageBig() {
        return faceImageBig;
    }

    /**
     * @param faceImageBig
     */
    public void setFaceImageBig(String faceImageBig) {
        this.faceImageBig = faceImageBig;
    }

    /**
     * 获取昵称
     *
     * @return nickname - 昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称
     *
     * @param nickname 昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取新用户注册后默认后台生成二维码，并且上传到fastdfs
     *
     * @return qrcode - 新用户注册后默认后台生成二维码，并且上传到fastdfs
     */
    public String getQrcode() {
        return qrcode;
    }

    /**
     * 设置新用户注册后默认后台生成二维码，并且上传到fastdfs
     *
     * @param qrcode 新用户注册后默认后台生成二维码，并且上传到fastdfs
     */
    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }


}