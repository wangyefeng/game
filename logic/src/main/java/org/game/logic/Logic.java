package org.game.logic;

import io.netty.util.ResourceLeakDetector;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.game.common.Server;
import org.game.logic.data.mongodb.config.CfgService;
import org.game.logic.data.mongodb.config.Config;
import org.game.logic.handler.ClientMsgHandler;
import org.game.logic.handler.GateMsgHandler;
import org.game.logic.net.TcpServer;
import org.game.logic.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

@SpringBootApplication
@EnableCaching
public class Logic extends Server implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Logic.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private Collection<GateMsgHandler<?>> gateMsgHandlers;

    @Autowired
    private Collection<ClientMsgHandler<?>> clientMsgHandlers;

    @Autowired
    private ZooKeeper zooKeeper;

    @Autowired
    private ApplicationContext applicationContext;

    private static final String SERVICE_ROOT = "/logic";

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    /**
     * 初始化spring容器后，启动服务器
     *
     * @throws Exception 异常
     */
    @Override
    protected void start0(String[] args) throws Exception {
        initConfig();
        registerHandler();
        tcpServer.start();
    }

    @Override
    protected void afterStart() throws Exception {
        super.afterStart();
        registerService();
    }

    private void initConfig() {
        log.info("开始加载配置表...");
        long start = System.currentTimeMillis();
        Collection<CfgService> cfgServices = applicationContext.getBeansOfType(CfgService.class).values();
        cfgServices.forEach(CfgService::init);
        Config.reload(cfgServices);
        log.info("加载配置完成, 花费: {}毫秒", System.currentTimeMillis() - start);
    }

    public void reloadConfig() {
        initConfig();
    }

    private void registerService() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(SERVICE_ROOT, false) == null) {
            zooKeeper.create(SERVICE_ROOT, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String servicePath = SERVICE_ROOT + "/" + tcpServer.getHost() + ":" + tcpServer.getPort();
        String path = zooKeeper.create(servicePath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        log.info("zookeeper registry service success, path: {}", path);
    }

    protected void stop() throws Exception {
        tcpServer.close();
        ThreadPool.shutdown();
        SpringApplication.exit(applicationContext);
    }

    @Override
    public void run(String... args) {
        start(args);
    }

    private void registerHandler() {
        log.info("handler registering...");
        gateMsgHandlers.forEach(GateMsgHandler::register);// 注册gate handler
        clientMsgHandlers.forEach(ClientMsgHandler::register);// 注册client handler
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Logic.class);
        application.setRegisterShutdownHook(false);// 关闭Spring-boot的程序关闭处理策略
        application.run(args);
    }
}
