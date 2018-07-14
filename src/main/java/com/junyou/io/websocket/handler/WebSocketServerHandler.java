package com.junyou.io.websocket.handler;

import java.net.InetSocketAddress;

import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.google.protobuf.GeneratedMessageV3;
import com.junyou.cmd.InnerCmdType;
import com.junyou.io.GameSession;
import com.junyou.io.IOExecutorsManager;
import com.junyou.io.global.GameSessionManager;
import com.junyou.io.global.GsCountChecker;
import com.junyou.log.GameLog;
import com.junyou.session.SessionConstants;
import com.kernel.tunnel.io.IoMsgSender;
import com.message.MessageCode;
import com.message.MessageInfo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * wind
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

//    private static final String WEBSOCKET_PATH = "/websocket";
    private static final String WEBSOCKET_PATH = "";
    private WebSocketServerHandshaker handshaker;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object e) throws Exception {
        if (e instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) e;
            GameLog.error("close the channel: heartbeat {},type={}", ctx.channel(), event.state());
            ctx.close();
        }
    }

    /**
     * 建立连接 channelConnected
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        GameLog.info("{} connect", channel);
        String remoteIp = ((InetSocketAddress)channel.remoteAddress()).getAddress().getHostAddress();

        if(GsCountChecker.isFull() && GsCountChecker.notWhiteIp(remoteIp)){
            //在线人数已满，同时不在IP白名单内的直接断开连接
            channel.close();
        }else{
            GameSession session = GameSessionManager.getInstance().addSession(SessionConstants.NOMAL_SESSION_TYPE,channel);
            IOExecutorsManager.bindExecutorService(session);
            //设置IP值
            session.setIp(remoteIp);
        }
    }


    /** 断开连接 原先的channelClosed */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        GameLog.error("{} channelClosed", ctx.channel());
        GameSession session = GameSessionManager.getInstance().getSession(ctx.channel());
        if (session == null) {
            GameLog.error("{} channel closed: no session id founded", ctx.channel());
        } else {
            Long roleId = session.getRoleId();
            if(roleId > 0){
                // 处理是否需要下线通知
                //回收老session:
                exitNotify(session);

//                //跨服信息回收
//                if(KuafuManager.kuafuIng(roleId) || KuafuArena1v1SourceService.isInKuafuArena(roleId)){
//                    KuafuMsgSender.send2KuafuServer(roleId,roleId, InnerCmdType.INNER_KUAFU_LEAVE, true);
//                    if(KuafuArena1v1SourceService.isInMatch(roleId)){
//                        KuafuMatchMsgSender.send2KuafuMatchServer(roleId, InnerCmdType.KUAFU_ARENA_1V1_CANCEL_MATCH, true);
//                        KuafuMatchMsgSender.send2KuafuCompetitionMatchServer(roleId, InnerCmdType.KUAFU_ARENA_1V1_CANCEL_MATCH, true);
//                    }
//                }
//                if(KuafuManager.kuafuIng(roleId) || KuafuArena4v4SourceService.isInKuafuArena(roleId)){
//                    KuafuMsgSender.send2KuafuServer(roleId,roleId, InnerCmdType.INNER_KUAFU_LEAVE, false);
//                    if(KuafuArena4v4SourceService.isInMatch(roleId)){
//                        KuafuMatchMsgSender.send2KuafuMatchServer(roleId, InnerCmdType.KUAFU_ARENA_4V4_CANCEL_MATCH, true);
//                        KuafuMatchMsgSender.send2KuafuCompetitionMatchServer(roleId, InnerCmdType.KUAFU_ARENA_4V4_CANCEL_MATCH, true);
//                    }
//                }
//                Redis redis = GameServerContext.getRedis();
//                if(redis!=null){
//                    if(redis.get(RedisKey.getKuafuBossBindServerIdKey(roleId))!=null){
//                        KuafuServerInfo serverInfo =	KuafuRoleServerManager.getInstance().getBindServer(roleId);
//                        if(serverInfo!=null){
//                            KuafuMsgSender.send2KuafuServer(roleId, roleId,
//                                    InnerCmdType.KUAFUBOSS_EXIT, null);
//                        }
//                    }
//                    if(redis.get(RedisKey.getKuafuQunXianYanBindServerIdKey(roleId))!=null){
//                        KuafuServerInfo serverInfo =	KuafuRoleServerManager.getInstance().getBindServer(roleId);
//                        if(serverInfo!=null){
//                            KuafuMsgSender.send2KuafuServer(roleId, roleId,InnerCmdType.KUAFUQUNXIANYAN_EXIT, null);
//                        }
//                    }
//                    //巅峰之战跨服战玩家在比赛中途下线处理
//                    if (KuafuManager.kuafuIng(roleId) && redis.exists(RedisKey.KUAFU_DIANFENG_SERVER_ID)) {
//                        String kfDianFengServerId = KuafuDianFengUtils.getDianFengKuaFuServerId();
//                        KuafuServerInfo kfServerInfo = KuafuRoleServerManager.getInstance().getBindServer(roleId);
//                        if (null != kfServerInfo && kfServerInfo.getServerId().equals(kfDianFengServerId)) {
//                            //ChuanQiLog.info("巅峰之战跨服战期间,玩家{0}在比赛中途下线处理", roleId);
//                            KuafuMsgSender.send2KuafuServer(GameConstants.DEFAULT_ROLE_ID, roleId, InnerCmdType.KUAFU_DIANFENG_OFFINE, new Object[] { roleId });
//                        }
//                    }
//                }
//                KuafuManager.removeKuafu(roleId);
            }
            IOExecutorsManager.removebindExecutorService(session);
            GameLog.info("channelInactive {}/{} closed", ctx.channel(), session.getId());
        }

        //容错
        GameSessionManager.getInstance().removeSession(ctx.channel());
    }

    private void exitNotify(GameSession session) throws Exception {
        IoMsgSender.swap(new Object[]{InnerCmdType.NODE_INIT_OUT,null,0,1,1,session.getId(),session.getIp(),session.getRoleId(),null,null});
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception  {
        if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
        // 传统的HTTP接入
        else if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        // WebSocket接入
        else if(msg instanceof TextWebSocketFrame){
            String request = ((TextWebSocketFrame) msg).text();
            System.out.println(ctx.channel()+" received" + request);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // 判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }


        //二进制
        if(frame instanceof BinaryWebSocketFrame){

            BinaryWebSocketFrame b = (BinaryWebSocketFrame)frame;
            ByteBuf byteBuf = b.content();
//            int len = byteBuf.readableBytes();
            
            short cmd = byteBuf.readShort();
            int contentLen = byteBuf.readUnsignedShort();
            
            byte[] reqData = null;
            
            if(contentLen > 0){
            	reqData = new byte[contentLen];
                byteBuf.readBytes(reqData);
            }
            
            GeneratedMessageV3 reqMsg = MessageCode.parseMessage(cmd, reqData);
            
            Channel channel = ctx.channel();
			GameSession session = GameSessionManager.getInstance().getSession(channel);
            
            //需要预处理业务
			if(cmd == MessageCode.Login_C){
				MessageInfo.Login_C loginReq = (MessageInfo.Login_C)reqMsg;
				String accountId = loginReq.getAccountId();
				String serverId = loginReq.getServerId();   
				boolean is_chenmi = loginReq.getIsChenMi(); 
				
				GameSession inAccountSession = GameSessionManager.getInstance().getSession4UserId(accountId,serverId);
				//挤出判断
				if(null != inAccountSession){
					inAccountSession.setJichu(true);
					
					//断开连接
					inAccountSession.getChannel().close();
				}
				session.setChenmi(is_chenmi);
			}
//			else if(cmd == MessageCode.CREATE_ROLE || cmd == MessageCode.CLIENT_APPLY_ENTER_GAME){
//				//如果是创角、登陆指令,处理必要数据
//				
//				String account_id = session.getUserId();
//				String server_id = session.getServerId();
//				
//				msg[1] = new Object[]{account_id,server_id,msg[1]};
//			}
			
			IoMsgSender.swap(new Object[]{cmd,reqMsg,CmdGroupInfo.target_type_gs, CmdGroupInfo.source_type_client,CmdGroupInfo.broadcast_type_1,session.getId(),session.getIp(),session.getRoleId(),null,null});
			
//            try {
//				PersonProbuf.Login_C login = PersonProbuf.Login_C.parseFrom(req);
//				
//				ChuanQiLog.error("=============={}",login.getId());
//				
//				
//				
//				PersonProbuf.Login_S.Builder sclogin = PersonProbuf.Login_S.newBuilder();
//				sclogin.setTest1(1);
//				sclogin.setTest2(2);
//				sclogin.setTest3(true);
//				sclogin.setTest4(4);
//				
//				PersonProbuf.RoleVO.Builder roleVo = PersonProbuf.RoleVO.newBuilder();
//				roleVo.setName("1212");
//				sclogin.setRole1(roleVo);
//				
//				roleVo.setLevel(12);
//				sclogin.addRole2(roleVo);
//				
//				byte[] src = sclogin.build().toByteArray();
//				ByteBuf bufferNew = PooledByteBufAllocator.DEFAULT.heapBuffer(src.length+4);
//				bufferNew.writeShort(1);
//				bufferNew.writeShort(src.length);
//				bufferNew.writeBytes(src);
//				 
//				WebSocketFrame sendData =new BinaryWebSocketFrame(bufferNew);
//				ctx.writeAndFlush(sendData);
//				
//			} catch (InvalidProtocolBufferException e) {
//				e.printStackTrace();
//			}

            //TODO 转发到业务数据中去


//            IMessageReadable messageReadable = null;
//            short cmd = 0;
//            try {
//                MsgSendHead.MsgBase msgBase = MsgSendHead.MsgBase.parseFrom(req);
//                int msgCode = msgBase.getMsgId();
//                ByteString contentByteStr = msgBase.getData();
//                messageReadable = new CSMessage((short) msgCode,contentByteStr.toByteArray());
//                cmd = messageReadable.getMessageCode();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            IPlayer player = null;
//            if(MessageCode.CS_Login == cmd){
//                IPlayer newPlayer = new PlayerForWS();
//                newPlayer.setCtx(ctx);
//                ctx.attr(CHANNEL_SESSION_KEY).set(newPlayer);
//                ctx.fireChannelRegistered();
//                player = newPlayer;
//            }else{ //如果是登录时可以创建player
//                player = ctx.attr(CHANNEL_SESSION_KEY).get();
//                if(player == null) {
//                    LogUtil.error("请先登录");
//                    return;
//                }
//
//                if(player.getAccountId() == null){
//                    LogUtil.error("登录流程还未处理完！");
//                    return;
//                }
//            }
//
//            messageReadable.dispatch(player);
//
            return;
        }

        GameLog.error("{} frame types not supported", frame.getClass().getName());

    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // 如果HTTP解码失败，返回HHTP异常
        if (!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade").toLowerCase()))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // 构造握手响应返回，本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8889"+WEBSOCKET_PATH, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private static void sendHttpResponse(
            ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

//    private static String getWebSocketLocation(HttpRequest req) {
//        String location =  req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH;
//        return "ws://" + location;
////        if (WebSocketServer.SSL) {
////            return "wss://" + location;
////        } else {
////            return "ws://" + location;
////        }
//    }

    public WebSocketServerHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketServerHandshaker handshaker) {
        this.handshaker = handshaker;
    }
}

