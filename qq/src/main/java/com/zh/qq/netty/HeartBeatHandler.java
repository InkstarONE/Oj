package com.zh.qq.netty;

import com.zh.qq.SpringUtil;
import com.zh.qq.enums.MsgActionEnum;
import com.zh.qq.service.UserService;
import com.zh.qq.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


//继承ChannelInboundHandlerAdapter 从而不需要实现channelRead0方法   用于检测channel的心跳handler
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {




    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.READER_IDLE){
                System.out.println("进入读请求空闲");
            }else if (event.state() == IdleState.WRITER_IDLE){
                System.out.println("进入写请求空闲");
            }else if (event.state() == IdleState.ALL_IDLE){
                System.out.println("channel关闭前，users的数量为：" + ChatHandler.users.size());
                Channel channel = ctx.channel();
                channel.close();
                System.out.println("channel关闭后，users的数量为：" + ChatHandler.users.size());
            }
        }
    }
}
