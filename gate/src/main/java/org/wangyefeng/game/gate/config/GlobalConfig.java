package org.wangyefeng.game.gate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalConfig {

    @Value("${server.tcp-port:8888}")
    private int tcpPort;

    public int getTcpPort() {
        return tcpPort;
    }
}
