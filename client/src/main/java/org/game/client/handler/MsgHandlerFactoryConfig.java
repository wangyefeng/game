package org.game.client.handler;

import com.google.protobuf.Message;
import org.game.proto.MsgHandler;
import org.game.proto.MsgHandlerFactory;
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
