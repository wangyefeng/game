package org.game.logic.player.function;


public abstract class AbstractModule implements Module {

    public AbstractModule(FunctionService functionService) {
        functionService.registerModule(this);
    }
}
