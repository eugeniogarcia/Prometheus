package com.swisscom.heroes.prometheus;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "management.metrics.export.prometheus.pushgateway")
@Validated
public class PushGatewayProperties {

	private static final int DEFAULT_PUSH_RATE = 10;

	/**
	 * Required base url(s) of the PushGateway(s).
	 */
	@NotNull
	private List<URL> baseUrls;

	/**
	 * Required identifier for this application instance.
	 */
	@NotBlank
	private String job;

	/**
	 * Frequency with which to push metrics to PushGateway.
	 */
	private Duration pushRate = Duration.ofSeconds(DEFAULT_PUSH_RATE);

	/**
	 * Push metrics right before shut-down. Mostly useful for batch jobs.
	 */
	private boolean pushOnShutdown = true;

	/**
	 * Delete metrics from PushGateway when application is shut-down.
	 */
	private boolean deleteOnShutdown;

	/**
	 * Used to group metrics in pushGateway. (instance-index and space-name are added by default)
	 */
	private Map<String, String> groupingKeys = new HashMap<>();

	public List<URL> getBaseUrls() {
		return baseUrls;
	}

	public void setBaseUrls(final List<URL> baseUrls) {
		this.baseUrls = baseUrls;
	}

	public String getJob() {
		return job;
	}

	public void setJob(final String job) {
		this.job = job;
	}

	public Duration getPushRate() {
		return pushRate;
	}

	public void setPushRate(final Duration pushRate) {
		this.pushRate = pushRate;
	}

	public boolean isPushOnShutdown() {
		return pushOnShutdown;
	}

	public void setPushOnShutdown(final boolean pushOnShutdown) {
		this.pushOnShutdown = pushOnShutdown;
	}

	public boolean isDeleteOnShutdown() {
		return deleteOnShutdown;
	}

	public void setDeleteOnShutdown(final boolean deleteOnShutdown) {
		this.deleteOnShutdown = deleteOnShutdown;
	}

	public Map<String, String> getGroupingKeys() {
		return groupingKeys;
	}

	public void setGroupingKeys(final Map<String, String> groupingKeys) {
		this.groupingKeys = groupingKeys;
	}

}
