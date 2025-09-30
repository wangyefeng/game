package org.wyf.game.logic.database.repository;

import org.wyf.game.logic.database.Repository;
import org.wyf.game.logic.database.entity.CycleFunction;

import java.util.Collection;

public interface CycleFunctionRepository extends Repository<CycleFunction, CycleFunction.PK> {

    Collection<CycleFunction> findByPlayerId(int playerId);
}