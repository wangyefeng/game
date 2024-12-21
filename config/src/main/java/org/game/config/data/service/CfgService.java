package org.game.config.data.service;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.game.config.Configs;
import org.game.config.data.entity.Cfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置表服务基础类
 *
 * @author 王叶峰
 */
public abstract class CfgService<Entity extends Cfg<ID>, Repository extends CrudRepository<Entity, ID>, ID> {

    @Autowired
    protected Repository repository;

    protected Map<ID, Entity> map = new HashMap<>();

    @PostConstruct
    protected void init() {
        repository.findAll().forEach(cfg -> map.put(cfg.getId(), cfg));
    }

    public Entity getCfg(ID id) {
        return map.get(id);
    }

    public Collection<Entity> getAllCfg() {
        return map.values();
    }

    public void check(Configs config) throws Exception {
        // 创建验证工厂和验证器
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        map.values().forEach(entity -> {
            // 执行验证
            for (ConstraintViolation<Entity> violation : validator.validate(entity)) {
                System.out.println("错误: " + violation.getMessage());
            }
        });

    }
}
