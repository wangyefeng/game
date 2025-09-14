package org.game.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Value("${logic.host}")
    private String host;

    @Value("${logic.server-id}")
    private int logicId;

    public String getHost() {
        return host;
    }

    public int getLogicId() {
        return logicId;
    }
}
