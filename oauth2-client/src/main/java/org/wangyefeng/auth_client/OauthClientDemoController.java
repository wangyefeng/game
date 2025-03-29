package org.wangyefeng.auth_client;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class OauthClientDemoController {

    private static final Logger log = LoggerFactory.getLogger(OauthClientDemoController.class);

    @RequestMapping(path = "/hello")
    public String demo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication: {}", authentication);
 
        return "hello";
    }
}