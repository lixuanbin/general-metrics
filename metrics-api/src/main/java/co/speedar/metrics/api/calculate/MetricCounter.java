package co.speedar.metrics.api.calculate;

/**
 * 累加器埋点类型
 */
public interface MetricCounter {
	/**
	 * 累加器加1
	 */
	void increment();

	/**
	 * 累加器加delta
	 *
	 * @param delta
	 */
	void incrementBy(long delta);
}
