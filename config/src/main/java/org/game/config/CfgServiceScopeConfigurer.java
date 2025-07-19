package org.game.config;

import org.game.config.service.CfgService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CfgServiceScopeConfigurer implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 自动设置CfgService的scope为prototype，目的在于重载配置表的时候，不影响原有配置数据的使用
        for (String beanName : beanFactory.getBeanNamesForType(CfgService.class)) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        }
    }
}
