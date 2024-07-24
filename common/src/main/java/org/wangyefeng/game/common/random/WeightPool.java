package org.wangyefeng.game.common.random;

import java.util.*;

/**
 * 权重随机类
 * 
 * @param <E>
 */
public class WeightPool<E> {

	/**
	 * 随机池
 	 */
	private final Collection<EWeight> randomPool = new ArrayList<>();

	/**
	 * 总权重
	 */
	private int sumWeight = 0;

	/**
	 * 添加元素到随机池
	 * 
	 * @param e 元素
	 * @param weight 权重
	 */
	public void addPool(E e, int weight) {
		if (weight < 0) {
			throw new IllegalArgumentException("权重不能为负数");
		}
		if (weight > 0) {
			randomPool.add(new EWeight(e, weight));
			sumWeight += weight;
		}
	}
	
	// 检查空池
	private void checkEmptyPool() {
		if (randomPool.isEmpty()) {
			throw new RandomPoolNotEnoughException("随机池为空");
		}
	}

	/**
	 * 随机出一个元素
	 * 
	 * @return 随机出一个元素
	 */
	public E randomOne() {
		checkEmptyPool();
		return randomEWeightOne().e;
	}

	private E randomOneNotCheck() {                  
		return randomEWeightOne().e;
	}

	/**
	 * 随机出一个元素
	 * @return 随机出一个元素
	 */
	private EWeight randomEWeightOne() {
		int randVal = RandomUtil.random(0, sumWeight - 1);
		for (EWeight eWeight : randomPool) {
			int weight = eWeight.getWeight();
			if (randVal < weight) {
				return eWeight;
			}
			randVal -= weight;
		}
		throw new RuntimeException("随机逻辑出错！");
	}

	/**
	 * 随机出一组元素
	 * 
	 * @return 随机出一组元素
	 */
	public List<E> randomList(int count) {
		checkEmptyPool();
		if (count <= 0) {
			throw new IllegalArgumentException("count只能为正数！count：" + count);
		}
		List<E> result = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			result.add(randomOneNotCheck());
		}
		return result;
	}

	/**
	 * 按照给定的总权重随机出一个元素
	 * 
	 * @param weight 总权重
	 * @return 随机池的一个元素或者null
	 */
	public E randomBySumWeight(int weight) {
		if (weight <= sumWeight) {
			weight = sumWeight;
		}
		int randVal = RandomUtil.random(0, weight - 1);
		for (EWeight eWeight : randomPool) {
			if (randVal < eWeight.getWeight()) {
				return eWeight.e;
			}
			randVal -= eWeight.getWeight();
		}
		return null;
	}

	private class EWeight implements IWeight {

		/**
		 * 权重
		 */
		private final int weight;

		// 元素
		E e;

		public EWeight(E e, int weight) {
			if (weight <= 0) {
				throw new IllegalArgumentException("Weight must be a positive integer");
			}
			this.weight = weight;
			this.e = Objects.requireNonNull(e);
		}

		@Override
		public int getWeight() {
			return weight;
		}
	}

	/**
	 * 获取随机池数量
	 * 
	 * @return 随机池数量
	 */
	public int getPoolSize() {
		return randomPool.size();
	}

	/**
	 * 清空随机池
	 */
	public void clearPool() {
		randomPool.clear();
		sumWeight = 0;
	}

	/**
	 * 删除随机池的元素e
	 * 
	 * @param e 元素
	 * @return 是否成功
	 */
	public boolean removePool(E e) {
		Iterator<EWeight> iterator = randomPool.iterator();
		while (iterator.hasNext()) {
			EWeight eWeight = iterator.next();
			if (eWeight.e == e) {
				iterator.remove();
				sumWeight -= eWeight.getWeight();
				return true;
			}
		}
		return false;
	}

	/**
	 * 总权重
	 * 
	 * @return 总权重
	 */
	public int sumWeight() {
		return sumWeight;
	}
	
	/**
	 * 空池判断
	 * @return true：空池
	 */
	public boolean poolIsEmpty() {
		return randomPool.isEmpty();
	}

	/**
	 * 随机出一组元素
	 *
	 * @return 随机出一组元素
	 */
	public List<E> randomListNotRepeat(int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("count只能为正数！count：" + count);
		}
		if (randomPool.size() < count) {
			throw new IllegalArgumentException("奖池数量不足，奖池数量：" + randomPool.size() + " 随机数量：" + count);
		}
		List<E> result = new ArrayList<>();
		if (randomPool.size() == count) {
			randomPool.forEach(e -> result.add(e.e));
		} else {
			Collection<EWeight> temp = new ArrayList<>(randomPool);
			for (int i = 0; i < count; i++) {
				EWeight e = RandomUtil.randomByWeight(temp);
				temp.remove(e);
				result.add(e.e);
			}
		}
		return result;
	}
}