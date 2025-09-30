package org.wyf.game.logic.player;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceScopeConfigurer implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 自动设置GameService的scope为prototype，以便每个玩家都有自己的游戏服务者实例
        for (String beanName : beanFactory.getBeanNamesForType(GameService.class)) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        }
    }
}
