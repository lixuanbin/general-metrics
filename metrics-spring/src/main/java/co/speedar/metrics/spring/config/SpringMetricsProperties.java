package co.speedar.metrics.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "metrics.spring")
public class SpringMetricsProperties {
	// 如果@Profile注解选择使用Spring埋点，还可以通过设置application.yml关闭这个开关，
	// 切换为使用DefaultMetricsClient埋点，不支持运行时动态修改
	private boolean enabled = true;
	// 使用defaultmetricsclient的时候是否静音模式，默认打开
	private boolean muteDefaultClient = true;
	// 使用defaultmetricsclient的打印时间间隔
	private int defaultClientPeriod = 10000;

	public boolean isEnabled() {
		return enabled;
	}

	// 注意，Spring容器启动完成后在运行时再次设置这个值将不会起作用
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isMuteDefaultClient() {
		return muteDefaultClient;
	}

	public void setMuteDefaultClient(boolean muteDefaultClient) {
		this.muteDefaultClient = muteDefaultClient;
	}

	public int getDefaultClientPeriod() {
		return defaultClientPeriod;
	}

	public void setDefaultClientPeriod(int defaultClientPeriod) {
		this.defaultClientPeriod = defaultClientPeriod;
	}
}
