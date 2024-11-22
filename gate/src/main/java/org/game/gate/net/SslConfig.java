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
        if (enabled) {
            Resource certificateResource = resourceLoader.getResource(certificate);
            Resource certificatePrivateKeyResource = resourceLoader.getResource(certificatePrivateKey);
            try {
                sslContext = SslContextBuilder.forServer(certificateResource.getFile(), certificatePrivateKeyResource.getFile()).build();
            } catch (Exception e) {
                log.error("Failed to initialize SSL context", e);
                System.exit(-1);
            }
        }
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
