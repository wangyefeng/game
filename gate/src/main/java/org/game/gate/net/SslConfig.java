package org.game.gate.net;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SslConfig {

    private static final Logger log = LoggerFactory.getLogger(SslConfig.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${server.ssl.enabled}")
    private boolean enabled;

    @Value("${server.ssl.certificate}")
    private String certificate;

    @Value("${server.ssl.certificate-private-key}")
    private String certificatePrivateKey;

    private SslContext sslContext;

    @PostConstruct
    public void init() {
        if (!enabled) {
            return; // SSL未启用，直接返回
        }
        log.info("开启TLS/SSL加密！！！");
        try {
            Resource certificateResource = resourceLoader.getResource(certificate);
            Resource certificatePrivateKeyResource = resourceLoader.getResource(certificatePrivateKey);
            File certFile = certificateResource.getFile();
            File keyFile = certificatePrivateKeyResource.getFile();
            sslContext = SslContextBuilder.forServer(certFile, keyFile).build();
        } catch (Exception e) {
            log.error("初始化SSL上下文失败: {}", e.getMessage(), e);
            enabled = false;
            throw new RuntimeException("SSL上下文初始化失败", e);
        }
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
