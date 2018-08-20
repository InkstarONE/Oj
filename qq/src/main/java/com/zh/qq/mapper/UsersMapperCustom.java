package com.zh.qq.mapper;

import com.zh.qq.pojo.Users;
import com.zh.qq.pojo.vo.FriendRequestVO;
import com.zh.qq.pojo.vo.MyFriendsVO;
import com.zh.qq.utils.MyMapper;

import java.util.List;

public interface UsersMapperCustom extends MyMapper<Users> {
    public List<FriendRequestVO> queryFriendRequestList(String accpectUserId);

    public List<MyFriendsVO> queryMyFriends(String userId);

    public void batchUpdateMsgSigned(List<String> msgIdList);
}