package org.game.config.controller;

import org.game.common.http.HttpResp;
import org.game.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private Config config;

    @RequestMapping(value = "/reload")
    public HttpResp<?> reload() {
        try {
            config.reloadAllConfig();
        } catch (Exception e) {
            return HttpResp.fail(101, e.getMessage());
        }
        return HttpResp.success(null);
    }

}
