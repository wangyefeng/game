package org.game.gate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Value("${zookeeper.root-path}")
    private String servicePath;

    public String getServicePath() {
        return servicePath;
    }
}
