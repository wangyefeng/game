package org.wyf.game.logic.net;

import com.google.protobuf.Message;
import org.wyf.game.proto.MsgHandler;
import org.wyf.game.proto.MsgHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MsgHandlerFactoryConfig {

    @Autowired
    private List<MsgHandler<? extends Message>> handlers;

    @Bean
    public MsgHandlerFactory getMsgHandlerFactory() {
        return new MsgHandlerFactory(handlers);
    }
}
