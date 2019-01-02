package co.speedar.metrics.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import co.speedar.metrics.api.client.MetricsClient;
import co.speedar.metrics.spring.client.SpringMetricsClient;
import co.speedar.metrics.spring.config.SpringMetricsProperties;

@Configuration
@ConditionalOnClass(MetricsClient.class)
@EnableConfigurationProperties(SpringMetricsProperties.class)
@Profile("!prod")
public class SpringMetricsAutoConfiguration {
	@Bean("springMetricsClient")
	@ConditionalOnMissingBean
	public MetricsClient springMetricsClient() {
		return new SpringMetricsClient();
	}

	@Bean
	public SpringMetricsRegistry springMetricsRegister() {
		return new SpringMetricsRegistry();
	}
}
