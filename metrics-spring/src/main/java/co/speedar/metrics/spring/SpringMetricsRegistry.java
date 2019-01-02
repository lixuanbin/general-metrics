package co.speedar.metrics.spring;

import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.speedar.metrics.spring.calculate.SpringMetricGuage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
public class SpringMetricsRegistry implements MeterBinder {
	private static Logger log = LoggerFactory.getLogger(SpringMetricsRegistry.class);

	private MeterRegistry registry;

	@PreDestroy
	public void destroy() {
		if (registry != null && !registry.isClosed()) {
			registry.close();
		}
	}

	@Override
	public void bindTo(MeterRegistry registry) {
		if (this.registry == null) {
			this.registry = registry;
			log.info("MeterRegistry is set...");
		}
	}

	public void registerGauge(SpringMetricGuage g) {
		checkState();
		List<Tag> tags = getTags(g.getTagMap());
		Gauge.builder(g.getMetricsName(), g.getCallable(), SpringMetricGuage.metricFunc).tags(tags)
				.description(g.getDescription()).register(this.registry);
	}

	public Counter registerCounter(String metricsName, String description, SortedMap<String, String> tagMap) {
		checkState();
		List<Tag> tags = getTags(tagMap);
		return Counter.builder(metricsName).tags(tags).description(description).register(this.registry);
	}

	public Timer registerTimer(String metricsName, String description, SortedMap<String, String> tagMap) {
		checkState();
		List<Tag> tags = getTags(tagMap);
		return Timer.builder(metricsName).tags(tags).description(description).register(this.registry);
	}

	private List<Tag> getTags(SortedMap<String, String> tagMap) {
		return tagMap.entrySet().stream().map(entry -> Tag.of(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private void checkState() {
		if (this.registry == null) {
			throw new IllegalStateException("Metrics registry is not initialized yet!");
		}
	}
}
