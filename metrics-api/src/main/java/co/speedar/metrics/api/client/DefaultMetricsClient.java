package co.speedar.metrics.api.client;

import java.util.Collections;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import co.speedar.metrics.api.calculate.MetricCounter;
import co.speedar.metrics.api.calculate.MetricTimer;

/**
 * 默认埋点实现，打印数据到控制台，仅作参考
 */
public class DefaultMetricsClient implements MetricsClient {
	private Timer timer;
	private int period;
	private boolean mute;

	public DefaultMetricsClient() {
		this(10000);
	}

	public DefaultMetricsClient(int period) {
		this(period, false);
	}

	public DefaultMetricsClient(int period, boolean mute) {
		System.out.println("default metrics client created"); // NOSONAR
		timer = new Timer("default_metrics_client_timer", true);
		this.period = period;
		this.mute = mute;
	}

	@Override
	public MetricCounter counter(String metricsName, String description, SortedMap<String, String> tagMap) {
		return new MetricCounter() {
			private AtomicLong c = new AtomicLong(0);
			private String mn = metricsName;
			private String d = description;
			private SortedMap<String, String> t = tagMap;

			@Override
			public void increment() {
				if (!mute) {
					System.out.println(// NOSONAR
							"metric name: " + mn + ", description: " + d
									+ (t != null && !t.isEmpty() ? ", tags: " + t.toString() : "") + ", counter: "
									+ c.incrementAndGet());
				}
			}

			@Override
			public void incrementBy(long delta) {
				if (!mute) {
					System.out.println(// NOSONAR
							"metric name: " + mn + ", description: " + d
									+ (t != null && !t.isEmpty() ? ", tags: " + t.toString() : "") + ", counter: "
									+ c.addAndGet(delta));
				}
			}
		};
	}

	@Override
	public MetricTimer timer(String metricsName, String description, SortedMap<String, String> tagMap) {
		return new MetricTimer() {
			private int limit = 1_000_000;
			private SortedMap<Long, Long> storage = buildStorage();

			private SortedMap<Long, Long> buildStorage() {
				return Collections.synchronizedSortedMap(new TreeMap<>());
			}

			@Override
			public void record(long millis) {
				if (storage.size() >= limit) {
					storage = buildStorage();
				}
				storage.put(System.currentTimeMillis(), millis);
				if (!mute) {
					System.out.println("time consumed: " + millis + " ms."); // NOSONAR
				}
			}

			@Override
			public void record(long time, TimeUnit unit) {
				this.record(TimeUnit.MILLISECONDS.convert(time, unit));
			}
		};
	}

	@Override
	public void gauge(String metricsName, String description, SortedMap<String, String> tagMap,
			Callable<Double> callable) {
		try {
			if (!mute) {
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						try {
							System.out.println(// NOSONAR
									"metric name: " + metricsName + ", description: " + description
											+ (tagMap != null && !tagMap.isEmpty() ? ", tags: " + tagMap.toString()
													: "")
											+ ", value: " + callable.call());
						} catch (Exception e) {
							e.printStackTrace(); // NOSONAR
						}
					}
				}, 0, period);
			}
		} catch (Exception e) {
			e.printStackTrace(); // NOSONAR
		}
	}

}