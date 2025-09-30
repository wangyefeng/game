package org.wyf.game.logic.database.repository;

import org.wyf.game.logic.database.Repository;
import org.wyf.game.logic.database.entity.DbTask;

import java.util.Collection;

public interface DbTaskRepository extends Repository<DbTask, DbTask.PK> {

    Collection<DbTask> findByPlayerId(int id);
}