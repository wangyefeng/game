package org.wangyefeng.game.gate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wangyefeng.game.gate.net.client.LogicClient;

@Configuration
public class BeanConfig {

    @Value("${client.logic.url}")
    private String url;

    @Bean
    public LogicClient logicClient() {
        String[] urlParts = url.split(":");
        return new LogicClient(urlParts[0], Integer.parseInt(urlParts[1]));
    }
}
