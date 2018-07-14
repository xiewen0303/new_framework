package com.junyou.kuafuio.handle;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import com.junyou.constants.GameConstants;
import com.junyou.log.GameLog;
import com.junyou.utils.GameStartConfigUtil;
import com.junyou.utils.exception.GameCustomException;
import com.kernel.pool.executor.ThreadNameFactory;

public class KuafuNetListener {

	private static EventLoopGroup bossGroup = new NioEventLoopGroup(GameConstants.THREAD_NUM,new ThreadNameFactory("netty-kuafu-boss-"));
	private static EventLoopGroup workerGroup = new NioEventLoopGroup(GameConstants.THREAD_NUM,new ThreadNameFactory("netty-kuafu-worker-"));
	
	public static void start(){
		int port = GameStartConfigUtil.getClientIoPort();
		
		try {
			// Configure the server.
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_REUSEADDR, true);
			bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			
			bootstrap.childHandler(new KuafuServerChannelPipelineFactory());
			
			
			// Bind and start to accept incoming connections.
			try {
				bootstrap.bind(new InetSocketAddress(port)).sync();
				GameLog.info("跨服初始化TCP服务完毕[{}]",port);
			} catch (Exception e) {
				GameLog.info("跨服初始化TCP服务失败");
				throw new GameCustomException(e);
			}
			
		} catch (Exception e) {
			
			String errorMsg = "kuafu's io failed to listen to port[" + port + "]";
			GameLog.error(errorMsg,e);
			
			throw new RuntimeException(errorMsg, e);
			
		}

		GameLog.error("kuafu io is listening to port[" + port + "]");
	}
	
	public static void stop(){
		try {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		} catch (Exception e) {
			GameLog.error("KuafuNetListener stop error",e);
		}
	}
}
