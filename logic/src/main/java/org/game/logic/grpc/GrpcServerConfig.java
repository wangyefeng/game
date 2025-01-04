package org.game.logic.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
public class GrpcServerConfig {

    @Autowired
    private Collection<BindableService> rpcServices;

    @Bean
    public Server grpcServer() {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(50051);
        for (BindableService rpcService : rpcServices) {
            serverBuilder.addService(rpcService);
        }
        return serverBuilder.build();
    }
}
