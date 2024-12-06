package org.game.logic.net;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TcpServerProperties.class)
public class TcpServerConfiguration {

    @Bean
    public TcpServer tcpServer(TcpServerProperties properties) {
        return new TcpServer(properties.getHost(), properties.getPort());
    }
}