package io.yzecho.nettychat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author yecho
 */
@Component
public class NettyServer {

    @Autowired
    @Qualifier("bootstrap")
    private ServerBootstrap bootstrap;

    private Channel serverChannel;

    public void start() {
        System.out.println("netty启动");
        try {
            serverChannel = bootstrap.bind(8888).sync().channel().closeFuture().sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void close() {
        serverChannel.close();
        serverChannel.parent().close();
    }


}
