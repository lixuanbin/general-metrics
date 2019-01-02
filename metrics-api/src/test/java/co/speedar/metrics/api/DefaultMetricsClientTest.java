package co.speedar.metrics.api;

import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import co.speedar.metrics.api.calculate.MetricCounter;
import co.speedar.metrics.api.calculate.MetricTimer;
import co.speedar.metrics.api.client.DefaultMetricsClient;
import co.speedar.metrics.api.client.MetricsClient;

public class DefaultMetricsClientTest {
	private MetricsClient client;

	@BeforeClass
	public void setup() {
		client = new DefaultMetricsClient(1000, false);
	}

	@Test
	public void testGauge() throws InterruptedException {
		client.gauge("hehe", "desc", new TreeMap<>(), () -> ThreadLocalRandom.current().nextDouble(100.0));
		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void testCounter() {
		MetricCounter counter = client.counter("hehe", "desc", new TreeMap<>());
		counter.increment();
		counter.incrementBy(101);
	}

	@Test
	public void testTimer() {
		MetricTimer timer = client.timer("haha", "desc", new TreeMap<>());
		timer.record(123L);
	}
}
