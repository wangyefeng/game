package org.wyf.game.logic.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;

@Entity
@IdClass(CycleFunction.PK.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CycleFunction implements Cloneable {

    @Id
    private int id;

    @Id
    private int playerId;

    private LocalDate resetDate;

    public CycleFunction() {
        // for JPA
    }

    public CycleFunction(int playerId, int id, LocalDate resetDate) {
        this.id = id;
        this.playerId = playerId;
        this.resetDate = resetDate;
    }

    @Override
    public CycleFunction clone() throws CloneNotSupportedException {
        return (CycleFunction) super.clone();
    }

    public int getId() {
        return id;
    }

    public LocalDate getResetDate() {
        return resetDate;
    }

    public void setResetDate(LocalDate resetDate) {
        this.resetDate = resetDate;
    }

    public int getPlayerId() {
        return playerId;
    }

    public record PK(int playerId, int id) {
    }
}
