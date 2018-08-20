package com.zh.qq.utils;

public class AdminJsonToContentUtil {

    public static String adminJsonToContent(String json) {
        //{"content":"213"}
        return json.substring(12,json.length()-2);
    }

    public static void main(String[] args) {
        String json = "{\"content\":\"213\"}";
        String retString = null;
        System.out.println(json.charAt(12));
        System.out.println(json.charAt(json.length()-3));
        System.out.println(json.substring(12,json.length()-2));
    }
}
