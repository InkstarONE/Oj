package com.zh.qq.service;

import com.zh.qq.enums.MsgActionEnum;
import com.zh.qq.enums.MsgSignFlagEnum;
import com.zh.qq.enums.SearchFriendsStatusEnum;
import com.zh.qq.mapper.*;
import com.zh.qq.netty.ChatMsg;
import com.zh.qq.netty.DataContent;
import com.zh.qq.netty.UserChannelRel;
import com.zh.qq.pojo.FriendsRequest;
import com.zh.qq.pojo.MyFriends;
import com.zh.qq.pojo.Users;
import com.zh.qq.pojo.vo.FriendRequestVO;
import com.zh.qq.pojo.vo.MyFriendsVO;
import com.zh.qq.utils.FastDFSClient;
import com.zh.qq.utils.FileUtils;
import com.zh.qq.utils.JsonUtils;
import com.zh.qq.utils.QRCodeUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private ChatMsgMapper chatMsgMapper;


    @Autowired
    private FastDFSClient fastDFSClient;



    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);

        return result != null ? true : false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria  criteria= userExample.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = (Users) usersMapper.selectOneByExample(userExample);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users saveUser(Users user) {
        String userId = sid.nextShort();
        //为每一个用户生成唯一二维码
        String qrCodePath = "/Users/zhanghao/Desktop/image/" + userId + "qrcode.png";
        // zh_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath, "zh_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setQrcode(qrCodeUrl);
        user.setId(userId);
        usersMapper.insert(user);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users users) {
        usersMapper.updateByPrimaryKeySelective(users);
        return queryUserById(users.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserById(String userId){
        return usersMapper.selectByPrimaryKey(userId);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUsername) {
        Users user = queryUserInfoByUsername(friendUsername);
        //1.不存在 2.自己 返回 3.已经是好友
        if (user == null){
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }

        if (myUserId.equals(user.getId())){
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }

        Example mfe = new Example(MyFriends.class);
        Example.Criteria mfc = mfe.createCriteria();
        mfc.andEqualTo("id",myUserId);
        mfc.andEqualTo("myFriendUserId",user.getId());
        MyFriends myFriendsRel = myFriendsMapper.selectOneByExample(mfe);
        if (myFriendsRel != null){
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfoByUsername(String username){
        Example ue = new Example(Users.class);
        Example.Criteria uc = ue.createCriteria();
        uc.andEqualTo("username",username);
        return usersMapper.selectOneByExample(ue);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {

        //根据朋友用户名查询
            Users youFriend = queryUserInfoByUsername(friendUsername);

            //查询发送好友记录表
        Example fre = new Example(FriendsRequest.class);
        Example.Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId",myUserId);
        frc.andEqualTo("acceptUserId",youFriend.getId());
        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(fre);

        if (friendsRequest == null){
            //不是你的好友 没有请求记录 则添加关系
            String requestId = sid.nextShort();

            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(youFriend.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }

    }

    //查询好友请求
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVO> queryFriendRequestList(String accpectUserId) {
        return usersMapperCustom.queryFriendRequestList(accpectUserId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
        Example fre = new Example(FriendsRequest.class);
        Example.Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId",sendUserId);
        frc.andEqualTo("acceptUserId",acceptUserId);
        friendsRequestMapper.deleteByExample(fre);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        saveFriends(sendUserId,acceptUserId);
        saveFriends(acceptUserId,sendUserId);
        deleteFriendRequest(sendUserId,acceptUserId);


        Channel sendChannel = UserChannelRel.get(sendUserId);
        if (sendChannel != null){
            //使用websocket主动推送消息到请求发送者 ，更新他的通讯录为最新
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

            sendChannel.writeAndFlush(
                    new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
        }

    }


    @Transactional(propagation = Propagation.REQUIRED)
    private void saveFriends(String sendUserId, String acceptUserId){
        MyFriends myFriends = new MyFriends();
        myFriends.setId(sid.nextShort());
        myFriends.setMyFriendUserId(acceptUserId);
        myFriends.setMyUserId(sendUserId);
        myFriendsMapper.insert(myFriends);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {
        List<MyFriendsVO> myFriends = usersMapperCustom.queryMyFriends(userId);
        return myFriends;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveMsg(ChatMsg chatMsg) {
        com.zh.qq.pojo.ChatMsg msgDB = new com.zh.qq.pojo.ChatMsg();
        //通过sid 生成id
        String msgId = sid.nextShort();
        msgDB.setId(msgId);
        msgDB.setAcceptUserId(chatMsg.getReceiverId());
        msgDB.setSendUserId(chatMsg.getSenderId());
        msgDB.setCreateTime(new Date());
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
        msgDB.setMsg(chatMsg.getMsg());
        chatMsgMapper.insert(msgDB);
        return msgId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {
            usersMapperCustom.batchUpdateMsgSigned(msgIdList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<com.zh.qq.pojo.ChatMsg> getUnReadMsgList(String acceptUerId) {
        Example chatExample = new Example(com.zh.qq.pojo.ChatMsg.class);
        Example.Criteria chatCriteria = chatExample.createCriteria();
        chatCriteria.andEqualTo("signFlag",0);
        chatCriteria.andEqualTo("acceptUserId",acceptUerId);
        List<com.zh.qq.pojo.ChatMsg> result = chatMsgMapper.selectByExample(chatExample);
        return result;
    }
}
