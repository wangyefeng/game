package org.wangyefeng.game.common.random;

public class Weight implements IWeight {
	
	private final int weight;
	
	public Weight(int weight) {
		if (weight <= 0) {
			throw new IllegalArgumentException("Weight must be a positive integer");
		}
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}
}
