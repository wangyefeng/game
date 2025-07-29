package org.game.logic.entity;

import java.time.LocalDate;

public class DbCycleFunction implements Cloneable {

    private int id;

    private LocalDate resetDate;

    public DbCycleFunction(int id, LocalDate resetDate) {
        this.id = id;
        this.resetDate = resetDate;
    }

    @Override
    public DbCycleFunction clone() throws CloneNotSupportedException {
        return (DbCycleFunction) super.clone();
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
}
