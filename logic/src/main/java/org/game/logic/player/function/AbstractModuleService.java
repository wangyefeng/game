package org.game.logic.player.function;

import org.game.logic.AbstractGameService;
import org.game.logic.entity.Entity;
import org.springframework.data.repository.CrudRepository;

public abstract class AbstractModuleService<E extends Entity, R extends CrudRepository<E, Integer>> extends AbstractGameService<E, R> implements Module {

    @Override
    public void init() {
        super.init();
        player.getService(FunctionService.class).registerModule(this);
    }
}
