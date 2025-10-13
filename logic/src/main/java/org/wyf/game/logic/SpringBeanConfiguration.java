package org.wyf.game.logic;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wyf.game.logic.net.TcpServer;

import java.util.Collection;

@Configuration
@EnableConfigurationProperties(LogicConfig.class)
public class SpringBeanConfiguration {

    @Autowired
    private LogicConfig logicConfig;

    @Autowired
    private Collection<BindableService> rpcServices;

    @Bean
    public TcpServer tcpServer() {
        return new TcpServer(logicConfig.getTcpPort());
    }

    @Bean
    public Server grpcServer() {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(logicConfig.getRpcPort());
        for (BindableService rpcService : rpcServices) {
            serverBuilder.addService(rpcService);
        }
        return serverBuilder.build();
    }
}
