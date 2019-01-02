package co.speedar.metrics.spring.calculate;

import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import co.speedar.metrics.api.calculate.MetricTimer;
import io.micrometer.core.instrument.Timer;

public class SpringMetricTimer implements MetricTimer {
	private String metricName;
	private SortedMap<String, String> tagMap;
	private Timer timer;

	public SpringMetricTimer(String metricName, SortedMap<String, String> tagMap, Timer timer) {
		this.metricName = metricName;
		this.tagMap = tagMap;
		this.timer = timer;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public SortedMap<String, String> getTagMap() {
		return tagMap;
	}

	public void setTagMap(SortedMap<String, String> tagMap) {
		this.tagMap = tagMap;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	@Override
	public void record(long millis) {
		this.record(millis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void record(long time, TimeUnit unit) {
		timer.record(time, unit);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SpringMetricTimer that = (SpringMetricTimer) o;
		return Objects.equals(metricName, that.metricName) && Objects.equals(tagMap, that.tagMap);
	}

	@Override
	public int hashCode() {
		return Objects.hash(metricName, tagMap);
	}

	@Override
	public String toString() {
		return "SpringMetricTimer{" + "metricName='" + metricName + '\'' + ", tagMap="
				+ (tagMap != null && !tagMap.isEmpty() ? tagMap.toString() : "{}") + '}';
	}
}
