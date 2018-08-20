package com.zh.qq.controller;

import com.zh.qq.enums.OperatorFriendRequestTypeEnum;
import com.zh.qq.enums.SearchFriendsStatusEnum;
import com.zh.qq.pojo.ChatMsg;
import com.zh.qq.pojo.Users;
import com.zh.qq.pojo.bo.UsersBO;
import com.zh.qq.pojo.vo.MyFriendsVO;
import com.zh.qq.pojo.vo.UsersVO;
import com.zh.qq.service.UserService;
import com.zh.qq.utils.FastDFSClient;
import com.zh.qq.utils.FileUtils;
import com.zh.qq.utils.JSONResult;
import com.zh.qq.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registerOrLogin")
    public JSONResult registerOrLogin(@RequestBody Users user) throws Exception {
        if (StringUtils.isBlank(user.getUsername()) ||StringUtils.isBlank(user.getPassword())){
            return JSONResult.errorMsg("用户名或密码不能为空");
        }

        boolean usernameIsexit = userService.queryUsernameIsExist(user.getUsername());

        Users usersResult = null;
        if (usernameIsexit){
            //登入
                usersResult = userService.queryUserForLogin(user.getUsername()
                        , MD5Utils.getMD5Str(user.getPassword()));
                if (usersResult == null){
                    return JSONResult.errorMsg("用户名或密码不正确");
                }
        }else {
            //注册
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            usersResult = userService.saveUser(user);

        }

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(usersResult,usersVO);
        return JSONResult.ok(usersVO);
    }

    @PostMapping("/uploadFaceBase64")
    public JSONResult uploadFaceBase64(@RequestBody UsersBO usersBO) throws Exception{
        String base64Data = usersBO.getFaceData();
        String userFacePath = "/Users/zhanghao/Desktop/image/" + usersBO.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath,base64Data);

       MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        String tump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + tump + arr[1];


        Users user = new Users();
        user.setId(usersBO.getUserId());
        user.setFaceImage(thumpImgUrl);;
        user.setFaceImageBig(url);
        Users result = userService.updateUserInfo(user);
        return JSONResult.ok(result);
    }

    @PostMapping("/setNickname")
    public JSONResult setNickname(@RequestBody UsersBO usersBO) throws Exception{
        Users user = new Users();
        user.setId(usersBO.getUserId());
        user.setNickname(usersBO.getNickname());
        Users result = userService.updateUserInfo(user);
        return JSONResult.ok(result);
    }


    //搜索好友 根据账号做匹配查询 非模糊查询
    @PostMapping("/search")
    public JSONResult searchUser(String myUserId,String friendUsername) throws Exception{
        if (StringUtils.isBlank(myUserId) ||StringUtils.isBlank(friendUsername)){
            return JSONResult.errorMsg("用户名不能为空");
        }

        //1.不存在 2.自己 返回 3.已经是好友
        Integer status = userService.preconditionSearchFriends(myUserId,friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status){
                Users user = userService.queryUserInfoByUsername(friendUsername);
                UsersVO usersVO = new UsersVO();
                BeanUtils.copyProperties(user,usersVO);
                return JSONResult.ok(usersVO);
        }else {
            String errMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(errMsg);
        }


    }




    //搜索好友 根据账号做匹配查询 非模糊查询  建立关系
    @PostMapping("/addFriendRequest")
    public JSONResult addFriendRequest(String myUserId,String friendUsername) throws Exception{
        if (StringUtils.isBlank(myUserId) ||StringUtils.isBlank(friendUsername)){
            return JSONResult.errorMsg("用户名不能为空");
        }

        //1.不存在 2.自己 返回 3.已经是好友
        Integer status = userService.preconditionSearchFriends(myUserId,friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status){
           userService.sendFriendRequest(myUserId,friendUsername);

        }else {
            String errMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(errMsg);
        }

        return JSONResult.ok();

    }


    //发送添加好友请求
    @PostMapping("/queryFriendRequests")
    public JSONResult queryFriendRequests(String userId) throws Exception{
        if (StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("传入id为空");
        }
        //查询用户接收到的朋友申请
        return JSONResult.ok(userService.queryFriendRequestList(userId));
    }


    //通过或者忽略请求
    @PostMapping("/operFriendRequest")
    public JSONResult operFriendRequest(String acceptUserId,String sendUserId,
                                        Integer operType) throws Exception{

        if (StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId)
                || operType == null){
            return JSONResult.errorMsg("传入id为空");
        }

        //如果没有对应的值则抛出空错误异常operType
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))){
            return JSONResult.errorMsg("传入id为空");
        }

        //忽略 则删除    数据库记录
        if (operType == OperatorFriendRequestTypeEnum.IGNORE.type){
                userService.deleteFriendRequest(sendUserId,acceptUserId);
        }

        //通过  则添加进数据库
        else if (operType == OperatorFriendRequestTypeEnum.PASS.type){
            userService.passFriendRequest(sendUserId,acceptUserId);
        }


        //查询用户接收到的朋友申请
        List<MyFriendsVO> myFriends = userService.queryMyFriends(acceptUserId);
        return JSONResult.ok(myFriends);

    }

    //查询我的好友
    @PostMapping("/myFriends")
    public JSONResult myFriends(String userId){
        if (StringUtils.isBlank(userId) ){
            return JSONResult.errorMsg("传入id为空");
        }

        List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);
        return JSONResult.ok(myFriends);
    }

    //获取未签收消息
    @PostMapping("/getUnReadMsgList")
    public JSONResult getUnReadMsgList(String acceptUserId){
        if (StringUtils.isBlank(acceptUserId) ){
            return JSONResult.errorMsg("传入id为空");
        }

        List<ChatMsg> unReadMsgList = userService.getUnReadMsgList(acceptUserId);
        return JSONResult.ok(unReadMsgList);
    }
}
