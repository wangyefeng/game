package org.wangyefeng.game.common.random;

import java.util.List;

/**
 * 权重随机类
 * 
 * @param <E>
 */
public class IWeightPool<E extends IWeight> {

	private WeightPool<E> randomPool = new WeightPool();
	/**
	 * 构造器
	 */
	public IWeightPool() {
	}

	/**
	 * 添加元素到随机池
	 *
	 * @param e
	 */
	public void addPool(E e) {
		randomPool.addPool(e, e.getWeight());
	}

	/**
	 * 随机出一个元素
	 *
	 * @return
	 */
	public E randomOne() {
		return randomPool.randomOne();
	}


	/**
	 * 随机出一组元素
	 *
	 * @return
	 */
	public List<E> randomList(int count) {
		return randomPool.randomList(count);
	}

	/**
	 * 随机出一组元素
	 *
	 * @return
	 */
	public List<E> randomListNotRepeat(int count) {
		return randomPool.randomListNotRepeat(count);
	}

	/**
	 * 按照给定的总权重随机出一个元素
	 *
	 * @param weight
	 * @return 随机池的一个元素或者null
	 */
	public E randomBySumWeight(int weight) {
		return randomPool.randomBySumWeight(weight);
	}

	/**
	 * 获取随机池数量
	 *
	 * @return
	 */
	public int getPoolSize() {
		return randomPool.getPoolSize();
	}

	/**
	 * 清空随机池
	 */
	public void clearPool() {
		randomPool.clearPool();
	}

	/**
	 * 删除随机池的元素e
	 *
	 * @param e
	 * @return 是否成功
	 */
	public boolean removePool(E e) {
		return randomPool.removePool(e);
	}

	/**
	 * 总权重
	 *
	 * @return
	 */
	public int sumWeight() {
		return randomPool.sumWeight();
	}

	/**
	 * 空池判断
	 * @return
	 */
	public boolean poolIsEmpty() {
		return randomPool.poolIsEmpty();
	}
}
