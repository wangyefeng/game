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

import java.io.IOException;
import java.io.InputStream;

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

    private volatile SslContext sslContext;

    @PostConstruct
    public void init() {
        if (!enabled) {
            return; // SSL未启用，直接返回
        }
        log.info("开启TLS/SSL加密！！！");
        buildSsl();
    }

    private void buildSsl() {
        InputStream c = null, k = null;
        try {
            Resource certificateResource = resourceLoader.getResource(certificate);
            c = certificateResource.getInputStream();
            Resource certificatePrivateKeyResource = resourceLoader.getResource(certificatePrivateKey);
            k = certificatePrivateKeyResource.getInputStream();
            sslContext = SslContextBuilder.forServer(c, k).build();
        } catch (Exception e) {
            enabled = false;
            sslContext = null;
            throw new RuntimeException("加载SSL证书失败, 请检查!", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                } if (k != null) {
                    k.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("加载SSL证书失败, 请检查!", e);
            }
        }
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public void reload() {
        log.info("重新加载SSL证书！！！");
        if (!enabled) {
            log.error("重新加载SSL证书失败: SSL加密未开启！");
            return;
        }
        buildSsl();
    }
}
