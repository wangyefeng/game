package org.wyf.game.logic.player.function;

import org.wyf.game.logic.database.Repository;
import org.wyf.game.logic.database.entity.BaseInfo;
import org.wyf.game.logic.player.AbstractGameService;

public abstract class AbstractModuleService<E extends BaseInfo, R extends Repository<E, Integer>> extends AbstractGameService<E, R> implements Module {

    @Override
    public void init() {
        super.init();
        player.getService(FunctionService.class).registerModule(this);
    }
}
