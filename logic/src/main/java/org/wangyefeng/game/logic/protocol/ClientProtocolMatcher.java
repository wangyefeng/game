package org.wangyefeng.game.logic.protocol;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.proto.AbstractProtocolInMatcher;
import org.wangyefeng.game.proto.InProtocol;

@Component
public class ClientProtocolMatcher extends AbstractProtocolInMatcher {

    @PostConstruct
    public void registerParsers() {
        for (InProtocol protocol : ClientProtocol.values()) {
            addParser(protocol);
        }
    }
}
