package org.game.logic.database.repository;

import org.game.logic.database.Repository;
import org.game.logic.database.entity.DbTask;

import java.util.Collection;

public interface DbTaskRepository extends Repository<DbTask, DbTask.PK> {

    Collection<DbTask> findByPlayerId(int id);
}