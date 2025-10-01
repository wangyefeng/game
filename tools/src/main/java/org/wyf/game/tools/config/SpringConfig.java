package org.wyf.game.tools.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "config")
public class SpringConfig {

    private String xlsxPath;

    @Value("${config.xlsx-path:#{null}}")  // 默认值
    public void setXlsxPath(String xlsxPath) {
        if (xlsxPath == null || xlsxPath.isEmpty()) {
            String userDir = System.getProperty("user.dir");
            this.xlsxPath = userDir + File.separatorChar + "share" + File.separatorChar + "config";
        } else {
            this.xlsxPath = xlsxPath;
        }
    }

    public String getXlsxPath() {
        return xlsxPath;
    }
}
