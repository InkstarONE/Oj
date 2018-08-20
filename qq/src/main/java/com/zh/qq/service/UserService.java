package com.zh.qq.service;

import com.zh.qq.netty.ChatMsg;
import com.zh.qq.pojo.Users;
import com.zh.qq.pojo.vo.FriendRequestVO;
import com.zh.qq.pojo.vo.MyFriendsVO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {

    //判断用户名是否存在
    public boolean queryUsernameIsExist(String username);

    //查询用户是否存在
    public Users queryUserForLogin(String username,String password);

    //用户注册
    public Users saveUser(Users user);

    //修改用户记录
    public Users updateUserInfo(Users users);

    public Users queryUserById(String userId);


    //搜索朋友 前置条件
    public Integer preconditionSearchFriends(String myUserId,String friendUsername);

    public Users queryUserInfoByUsername(String username);


    public void sendFriendRequest(String myUserId,String friendUsername);

    //查询好友请求
    public List<FriendRequestVO> queryFriendRequestList(String accpectUserId);


    //删除好友请求记录
    public void deleteFriendRequest(String sendUserId,String acceptUerId);

    //通过好友请求记录  逆向保存好友
    public void passFriendRequest(String sendUserId,String acceptUerId);

    public List<MyFriendsVO> queryMyFriends(String userId);

    //保存聊天消息到数据库
    public String saveMsg(ChatMsg chatMsg);

    //批量签收消息
    public void updateMsgSigned(List<String> msgIdList);


    //获取未签收消息列表
    public List<com.zh.qq.pojo.ChatMsg> getUnReadMsgList(String acceptUerId);
}
