package org.game.gate;

import io.netty.util.ResourceLeakDetector;
import org.apache.zookeeper.ZooKeeper;
import org.game.common.Server;
import org.game.gate.handler.client.ClientMsgHandler;
import org.game.gate.handler.logic.LogicMsgHandler;
import org.game.gate.net.TcpServer;
import org.game.gate.net.client.ClientGroup;
import org.game.gate.net.client.LogicClient;
import org.game.gate.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 网关服务器
 */
@SpringBootApplication
@ConfigurationPropertiesScan("org.game.gate.net")
public class Gate extends Server implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Gate.class);

    private static final String SERVICE_REGISTRY_ZNODE = "/logic";

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private Collection<LogicMsgHandler<?>> logicMsgHandlers;

    @Autowired
    private Collection<ClientMsgHandler<?>> clientMsgHandlers;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ZooKeeper zooKeeper;

    @Autowired
    private ClientGroup clientGroup;

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    @Override
    protected void start0(String[] args) {
        registerHandler();
        connectLogic();
    }

    private void connectLogic() {
        try {
            // 连接逻辑服
            String servicePath = SERVICE_REGISTRY_ZNODE;
            List<String> serviceNodes = zooKeeper.getChildren(servicePath, true);
            if (serviceNodes.isEmpty()) {
                log.info("等待逻辑服注册服务...");
                while (serviceNodes.isEmpty()) {
                    Thread.sleep(1000);
                    serviceNodes = zooKeeper.getChildren(servicePath, false);
                }
                log.info("逻辑服注册服务成功！ 地址：{}", serviceNodes.get(0));
            }
            for (String serverId : serviceNodes) {
                String path = SERVICE_REGISTRY_ZNODE + "/" + serverId;
                byte[] data = zooKeeper.getData(path, false, null);
                String serverInfo = new String(data);
                String[] address = serverInfo.split(":");
                LogicClient logicClient = new LogicClient(serverId, address[0], Integer.parseInt(address[1]));
                logicClient.start();
                clientGroup.add(logicClient);
            }
        } catch (Exception e) {
            throw new RuntimeException("连接逻辑服失败！", e);
        }
    }

    @Override
    protected void afterStart() {
        // 最后启动tcp服务
        tcpServer.start();
    }

    public void stop() throws Exception {
        tcpServer.close();
        clientGroup.close();
        ThreadPool.shutdown();
        SpringApplication.exit(applicationContext);
    }

    @Override
    public void run(String... args) {
        start(args);
    }

    private void registerHandler() {
        log.info("handler registering...");
        logicMsgHandlers.forEach(LogicMsgHandler::register);
        clientMsgHandlers.forEach(ClientMsgHandler::register);
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Gate.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }
}
