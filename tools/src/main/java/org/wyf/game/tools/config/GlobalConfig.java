package org.wyf.game.tools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "config")
public class GlobalConfig {

    private final String xlsxPath;

    /**
     * 配置表前缀
     */
    private final String prefix;

    public GlobalConfig(String xlsxPath, String prefix) {
        this.xlsxPath = xlsxPath(xlsxPath);
        this.prefix = prefix;
    }

    // 默认值
    private String xlsxPath(String xlsxPath) {
        if (xlsxPath == null || xlsxPath.isEmpty()) {
            return System.getProperty("user.dir") + File.separatorChar + "share" + File.separatorChar + "config";
        }
        return xlsxPath;
    }

    public String getXlsxPath() {
        return xlsxPath;
    }

    public String getPrefix() {
        return prefix;
    }
}
