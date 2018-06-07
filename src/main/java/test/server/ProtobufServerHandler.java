package test.server;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import test.proto.DriverTrace;
import test.proto.ParkPushMsg;

import java.util.UUID;

/**
 * Created by tianhong on 2018/5/11.
 */
public class ProtobufServerHandler extends ChannelInboundHandlerAdapter {

    //IdleStateHandler 心跳检测
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println(Thread.currentThread().getName() + " channel ProtobufServerHandler userEventTriggered");
                ctx.channel().close();
            }
        } else {
            //客户端主动断的时候会走到这
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            DriverTrace.DriverTraceMsg req = (DriverTrace.DriverTraceMsg) msg;
            System.out.println(Thread.currentThread().getName() + " channel ProtobufServerHandler channelRead request msg : " + req.toString());
            ParkPushMsg.ParkMsg.Builder parkMsgBuilder = ParkPushMsg.ParkMsg.newBuilder();
            parkMsgBuilder.setResponseId(UUID.randomUUID().toString());
            parkMsgBuilder.setTitle("title");
            parkMsgBuilder.setMessage("response msg");
            parkMsgBuilder.setType("type");
            ChannelFuture channelFuture = ctx.writeAndFlush(parkMsgBuilder.build());
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println(Thread.currentThread().getName() + " channel ProtobufServerHandler channelRead response");
                }
            });
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + " channel ProtobufServerHandler channelRead");
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(Thread.currentThread().getName() + " channel ProtobufServerHandler exceptionCaught");
        cause.printStackTrace();
        //发生异常，关闭链路
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //tcp链路建立成功
        //也可以先write 最后再一次性flush
        Channel channel = ctx.channel();
        System.out.println(Thread.currentThread().getName() + " channel ProtobufServerHandler channelActive");
        if (!ChannelManager.channelMap.containsKey(channel.id() + "")) {
            ChannelManager.channelMap.put(channel.id() + "", channel);
        }
    }
}
