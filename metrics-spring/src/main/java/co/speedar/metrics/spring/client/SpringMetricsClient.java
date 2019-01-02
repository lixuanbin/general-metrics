package co.speedar.metrics.spring.client;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import co.speedar.metrics.api.calculate.MetricCounter;
import co.speedar.metrics.api.calculate.MetricTimer;
import co.speedar.metrics.api.client.DefaultMetricsClient;
import co.speedar.metrics.api.client.MetricsClient;
import co.speedar.metrics.spring.SpringMetricsRegistry;
import co.speedar.metrics.spring.calculate.SpringMetricCounter;
import co.speedar.metrics.spring.calculate.SpringMetricGuage;
import co.speedar.metrics.spring.calculate.SpringMetricTimer;
import co.speedar.metrics.spring.config.SpringMetricsProperties;

public class SpringMetricsClient implements MetricsClient {
	private static Logger log = LoggerFactory.getLogger(SpringMetricsClient.class);
	@Autowired
	private SpringMetricsRegistry register;
	@Autowired
	private SpringMetricsProperties springMetricsProperties;
	private Set<SpringMetricGuage> gauges = ConcurrentHashMap.newKeySet();
	private ConcurrentMap<String, MetricCounter> counters = new ConcurrentHashMap<>();
	private ConcurrentMap<String, MetricTimer> timers = new ConcurrentHashMap<>();
	private MetricsClient defaultClient;

	@PostConstruct
	public void init() {
		if (!springMetricsProperties.isEnabled()) {
			defaultClient = new DefaultMetricsClient(springMetricsProperties.getDefaultClientPeriod(),
					springMetricsProperties.isMuteDefaultClient());
		}
	}

	@PreDestroy
	public void destroy() {
		gauges.clear();
		counters.clear();
		timers.clear();
	}

	@Override
	public void gauge(String metricName, String description, SortedMap<String, String> tagMap,
			Callable<Double> callable) {
		SpringMetricGuage g = new SpringMetricGuage(metricName, description, tagMap, callable);
		if (gauges.add(g)) {
			if (springMetricsProperties.isEnabled()) {
				register.registerGauge(g);
				log.info("gauge metric added for: {}", g);
			} else {
				defaultClient.gauge(metricName, description, tagMap, callable);
			}
		} else {
			log.warn("duplicated gauge: {}", g);
		}
	}

	@Override
	public MetricCounter counter(String metricName, String description, SortedMap<String, String> tagMap) {
		MetricCounter c;
		String key = getKey(metricName, tagMap);
		if ((c = counters.get(key)) == null) {
			c = counters.computeIfAbsent(key, k -> getSpringMetricCounter(metricName, description, tagMap));
		}
		return c;
	}

	@Override
	public MetricTimer timer(String metricName, String description, SortedMap<String, String> tagMap) {
		MetricTimer t;
		String key = getKey(metricName, tagMap);
		if ((t = timers.get(key)) == null) {
			t = timers.computeIfAbsent(key, k -> getSpringMetricTimer(metricName, description, tagMap));
		}
		return t;
	}

	private MetricTimer getSpringMetricTimer(String metricName, String description, SortedMap<String, String> tagMap) {
		if (springMetricsProperties.isEnabled()) {
			return new SpringMetricTimer(metricName, tagMap, register.registerTimer(metricName, description, tagMap));
		} else {
			return defaultClient.timer(metricName, description, tagMap);
		}
	}

	private MetricCounter getSpringMetricCounter(String metricName, String description,
			SortedMap<String, String> tagMap) {
		if (springMetricsProperties.isEnabled()) {
			return new SpringMetricCounter(metricName, tagMap,
					register.registerCounter(metricName, description, tagMap));
		} else {
			return defaultClient.counter(metricName, description, tagMap);
		}
	}

	private String getKey(String metricName, SortedMap<String, String> tagMap) {
		return metricName + (tagMap != null ? tagMap.toString() : "");
	}

	public Set<SpringMetricGuage> getGauges() {
		return gauges;
	}

	public Map<String, MetricCounter> getCounters() {
		return counters;
	}

	public Map<String, MetricTimer> getTimers() {
		return timers;
	}
}
