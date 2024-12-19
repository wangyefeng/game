package org.game.chat;

import io.netty.util.ResourceLeakDetector;
import org.game.common.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
public class Chat extends Server implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Chat.class);

    @Autowired
    private ApplicationContext applicationContext;

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    @Override
    protected void start0(String[] args) throws Exception {

    }

    @Override
    protected void afterStart() throws Exception {

    }

    public void stop() throws Exception {
        SpringApplication.exit(applicationContext);
    }

    @Override
    public void run(String... args) {
        start(args);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Chat.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }
}
