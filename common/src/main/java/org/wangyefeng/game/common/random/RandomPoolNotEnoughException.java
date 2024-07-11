package org.wangyefeng.game.common.random;

public class RandomPoolNotEnoughException extends RuntimeException {
	
	private static final long serialVersionUID = -3413844226295568578L;

	public RandomPoolNotEnoughException() {
	}
	
	public RandomPoolNotEnoughException(String message) {
		super(message);
	}
}
