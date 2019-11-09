package io.yzecho.nettychat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yecho
 */
@Configuration
public class NettyServerApplication {

    @Autowired
    ChatHandler chatHandler;

//    @Autowired
//    WebSocketHandler webSocketHandler;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup();
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    @Bean(name = "bootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // HTTP解编码器
                        socketChannel.pipeline().addLast(new HttpServerCodec());
                        // 异步写大数据流不引起高内存消耗
                        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                        // 聚合HTTP消息
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                        // 处理升级🤝
                        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/websocket"));
//                        socketChannel.pipeline().addLast(webSocketHandler);
                        socketChannel.pipeline().addLast(chatHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return bootstrap;
    }

}
