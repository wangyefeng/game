package org.game.logic;

import io.netty.util.ResourceLeakDetector;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.game.common.Server;
import org.game.logic.data.config.CfgService;
import org.game.logic.data.config.Config;
import org.game.logic.handler.ClientMsgHandler;
import org.game.logic.handler.GateMsgHandler;
import org.game.logic.net.TcpServer;
import org.game.logic.thread.ThreadPool;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@SpringBootApplication
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
        registerCfgService();
    }

    private void initConfig() {
        log.info("开始加载配置表...");
        long start = System.currentTimeMillis();
        Reflections reflections = new Reflections("org.game.logic.data.config.service");
        Set<Class<? extends CfgService>> classes = reflections.getSubTypesOf(CfgService.class);
        Collection<CfgService> services = new ArrayList<>(classes.size());
        for (Class<? extends CfgService> clazz : classes) {
            services.add(registerCfgService(clazz));
        }
        Config.reload(services);
        log.info("加载配置完成, 花费: {}毫秒", System.currentTimeMillis() - start);
    }

    private CfgService registerCfgService(Class<? extends CfgService> requiredType) {
        String name = Introspector.decapitalize(requiredType.getSimpleName());
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(requiredType);
        beanDefinitionBuilder.setScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE);// 设置为原型模式，重载配置表的时候不会影响到原有bean
        defaultListableBeanFactory.registerBeanDefinition(name, beanDefinitionBuilder.getBeanDefinition());
        return applicationContext.getBean(requiredType);
    }

    public void reloadConfig() {
        log.info("开始重新加载配置表...");
        Config.reload(applicationContext.getBeansOfType(CfgService.class).values());
        log.info("配置表重新加载完成");
    }

    private void registerCfgService() throws KeeperException, InterruptedException {
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
        application.setRegisterShutdownHook(false);// 关闭Spring-boot停服处理策略
        application.run(args);
    }
}
