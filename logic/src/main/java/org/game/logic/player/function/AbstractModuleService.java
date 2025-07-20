package org.game.logic.player.function;

import org.game.logic.database.Repository;
import org.game.logic.database.entity.Entity;
import org.game.logic.player.AbstractGameService;

public abstract class AbstractModuleService<E extends Entity, R extends Repository<E, Integer>> extends AbstractGameService<E, R> implements Module {

    @Override
    public void init() {
        super.init();
        player.getService(FunctionService.class).registerModule(this);
    }
}
