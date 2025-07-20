package org.game.config.controller;

import org.game.common.http.HttpResp;
import org.game.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @RequestMapping(value = "/reload")
    public HttpResp<?> reload() {
        try {
            configService.reload();
        } catch (Exception e) {
            return HttpResp.fail(101, e.toString());
        }
        return HttpResp.success(null);
    }

}
