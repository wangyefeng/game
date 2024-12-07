package org.game.gate.net;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import jakarta.annotation.PostConstruct;
import org.game.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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
        try {
            Resource certificateResource = resourceLoader.getResource(certificate);
            Resource certificatePrivateKeyResource = resourceLoader.getResource(certificatePrivateKey);
            File certFile = certificateResource.getFile();
            File keyFile = certificatePrivateKeyResource.getFile();
            sslContext = SslContextBuilder.forServer(certFile, keyFile).build();
            // 从 SslContext 中获取证书
            X509Certificate x509Certificate = getCertificateFromFile(certFile);
            log.info("SSL/TLS证书过期时间:{}", DateUtil.format(x509Certificate.getNotAfter()));
        } catch (Exception e) {
            enabled = false;
            sslContext = null;
            throw new RuntimeException("加载SSL证书失败, 请检查!", e);
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

    // 从证书文件加载 X509Certificate
    private X509Certificate getCertificateFromFile(File certFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(certFile)) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(fis);
        }
    }
}
