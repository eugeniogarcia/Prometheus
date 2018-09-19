package com.swisscom.heroes.prometheus;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;

class PrometheusPusher {

	private static final String TEAM_GROUP_KEY = "team";
	private static final String SPACE_GROUP_KEY = "space";
	private static final String INSTANCE_GROUP_KEY = "instance";
	private static final int INITIAL_DELAY = 1000;

	private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(PrometheusPusher.class);

	private final CollectorRegistry collectorRegistry;
	private final PushGatewayProperties pushGatewayProperties;
	private final List<PushGateway> pushGateways;
	private final ScheduledExecutorService executorService;
	private final Map<String, String> groupingKeys;
	private final Logger logger;

	PrometheusPusher(final CollectorRegistry collectorRegistry,
			final PushGatewayProperties pushGatewayProperties,
			final MetadataProperties environment) {
		this(collectorRegistry,
				pushGatewayProperties,
				environment,
				createPushGateways(pushGatewayProperties),
				DEFAULT_LOGGER);
	}

	PrometheusPusher(final CollectorRegistry collectorRegistry,
			final PushGatewayProperties pushGatewayProperties,
			final MetadataProperties environment,
			final List<PushGateway> pushGateways,
			final Logger logger) {
		this.collectorRegistry = collectorRegistry;
		this.pushGatewayProperties = pushGatewayProperties;
		this.pushGateways = pushGateways;
		this.groupingKeys = defineGroupingKeys(environment);
		this.logger = logger;
		this.executorService = Executors.newSingleThreadScheduledExecutor((Runnable runnable) -> {
			final Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			thread.setName("prometheus-push-gateway");
			return thread;
		});
		executorService.scheduleAtFixedRate(this::push,
				INITIAL_DELAY,
				pushGatewayProperties.getPushRate().toMillis(),
				TimeUnit.MILLISECONDS);

	}

	private static List<PushGateway> createPushGateways(final PushGatewayProperties pushGatewayProperties) {
		return pushGatewayProperties.getBaseUrls().stream()
				.map(PushGateway::new)
				.collect(Collectors.toList());
	}

	void push() {
		pushGateways.forEach(this::pushToSingleGateway);
	}

	@PreDestroy
	void shutdown() {
		executorService.shutdown();
		if (pushGatewayProperties.isPushOnShutdown()) {
			push();
		}
		if (pushGatewayProperties.isDeleteOnShutdown()) {
			pushGateways.forEach(this::deleteFromSingleGateway);
		}
	}

	private void pushToSingleGateway(final PushGateway pushGateway) {
		try {
			pushGateway.pushAdd(collectorRegistry, pushGatewayProperties.getJob(), groupingKeys);
		} catch (final UnknownHostException exception) {
			logger.error("Unable to locate host. No longer attempting metrics publication to this host", exception);
			executorService.shutdown();
		} catch (final IOException | RuntimeException exception) {
			logger.error("Unable to push metrics to Prometheus PushGateway", exception);
		}
	}

	private void deleteFromSingleGateway(final PushGateway pushGateway) {
		try {
			pushGateway.delete(pushGatewayProperties.getJob(), groupingKeys);
		} catch (final IOException | RuntimeException exception) {
			logger.error("Unable to delete metrics from Prometheus PushGateway", exception);
		}

	}

	private Map<String, String> defineGroupingKeys(final MetadataProperties environment) {
		final Map<String, String> extendedKeys = pushGatewayProperties.getGroupingKeys();
		extendedKeys.putIfAbsent(INSTANCE_GROUP_KEY, environment.getInstancia());
		extendedKeys.putIfAbsent(SPACE_GROUP_KEY, environment.getEntorno());
		extendedKeys.putIfAbsent(TEAM_GROUP_KEY, environment.getEquipo());
		return extendedKeys;
	}

}