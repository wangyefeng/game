package org.game.chat;

import org.game.common.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 网关服务器
 */
@SpringBootApplication
@ConfigurationPropertiesScan("org.game.gate.net")
public class Chat extends Server {

    private static final Logger log = LoggerFactory.getLogger(Chat.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void start0() throws Exception {
    }

    @Override
    protected void afterStart() throws Exception {

    }

    public void stop() throws Exception {
        SpringApplication.exit(applicationContext);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Chat.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }
}
