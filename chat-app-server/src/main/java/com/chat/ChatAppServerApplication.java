package com.chat;

import com.chat.netty.NettyServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@SpringBootApplication(scanBasePackages = {"com.chat"})
public class ChatAppServerApplication implements CommandLineRunner {

    @Value("${netty.host}")
    private String host;
    @Value("${netty.port}")
    private Integer port;

    @Resource
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(ChatAppServerApplication.class, args);
    }

    @Override
    public void run(String... strings) {
        InetSocketAddress address = new InetSocketAddress(host, port);
        ChannelFuture future = nettyServer.start(address);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                nettyServer.destroy();
            }
        });

        future.channel().closeFuture().syncUninterruptibly();
    }
}
