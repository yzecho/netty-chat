package io.yzecho.nettychat;

import io.yzecho.nettychat.netty.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author yecho
 */
@SpringBootApplication
public class NettyChatApplication {

    public static void main(String[] args) {
        // SpringApplication.run(NettyChatApplication.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(NettyChatApplication.class, args);
        NettyServer server = context.getBean(NettyServer.class);
        server.start();
    }

}
