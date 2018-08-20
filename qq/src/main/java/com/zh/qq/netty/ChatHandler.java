package com.zh.qq.netty;

import com.zh.qq.SpringUtil;
import com.zh.qq.enums.MsgActionEnum;
import com.zh.qq.service.UserService;
import com.zh.qq.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录管理客户端
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected ChatHandler() {
        super();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());//当客户端链接服务端之后 获取客户端channe了 放入 group管理
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        users.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //1.获取客户端信息发来的
        String content = msg.text();

        Channel currentChannel = ctx.channel();

        DataContent dataContent = JsonUtils.jsonToPojo(content,DataContent.class);
        Integer action = dataContent.getAction();

        //2.判断消息类型,根据不同类型处理不同业务
        if (action == MsgActionEnum.CONNECT.type){
            //2.1 当websocket第一次open时 初始化channel，把用户channel和userId关联起来
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelRel.put(senderId,currentChannel);

            //测试
            for (Channel c : users){
                System.out.println(c.id().asLongText());
            }
            UserChannelRel.output();
        }else if (action == MsgActionEnum.CHAT.type){
            //2.2 聊天类型的消息 存入数据库，同时标记消息的签收状态[未签收]
            ChatMsg chatMsg = dataContent.getChatMsg();
            String senderId = chatMsg.getSenderId();
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();

            //保存进数据库 并标记为未签收
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);


            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            //发送消息
            //从全局用户channel关系中获取接收方channel
            Channel receiverChannel = UserChannelRel.get(receiverId);
            if (receiverChannel == null){
                //channel 为空代表离线 推送消息

            }else {
                //receiverChannel不为空 去ChannelGroup查找是否存在
                Channel findChnnel = users.find(receiverChannel.id());
                if (findChnnel != null){
                    //用户在线
                    receiverChannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    JsonUtils.objectToJson(dataContentMsg)));
                }else {
                    //用户离线
                }
            }
        }
        else if (action == MsgActionEnum.SIGNED.type){
            //2.3 签收消息，针对具体的消息进行签收 修改数据库中对应的签收状态 已签收
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            //扩展字段在signedl类型消息中，代表需要去签收消息的id，逗号间隔
            String msgIdStr = dataContent.getExtend();
            String[] msgIds = msgIdStr.split(",");

            List <String> msgIdList = new ArrayList<>();
            for (String mid : msgIds){
                if (!StringUtils.isBlank(mid)){
                    msgIdList.add(mid);
                }
            }
            System.out.println(msgIdList.toString());

            if (msgIdList !=null && !msgIdList.isEmpty() && msgIdList.size() > 0){
                userService.updateMsgSigned(msgIdList);
            }
        }
        else if (action == MsgActionEnum.KEEPALIVE.type){
            //2.4 心跳类型的消息
            System.out.println("收到来自" + currentChannel +"心跳包");
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //发生异常之后 关闭channel 随后从channelgroup移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
