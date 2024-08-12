package org.game.common.random;


/**
 * 当随机池为空时抛出此异常
 *
 * @author 王叶峰
 * @date 2014-08-01
 * @see WeightArrayPool
 * @see WeightListPool
 */
public class EmptyPoolException extends RuntimeException {

    private static final long serialVersionUID = -3413844226295568578L;

    public EmptyPoolException() {
    }
}
