package com.zh.qq.utils;

public class QrCode {
    public static void main(String[] args) {
        //为每一个用户生成唯一二维码
        String qrCodePath = "/Users/zhanghao/Desktop/image/code.png";
        // zh_qrcode:[username]
        QRCodeUtils qrCodeUtils = new QRCodeUtils();
        qrCodeUtils.createQRCode(qrCodePath, "http://39.108.229.47:8080/chat/chat.apk");

    }
}
