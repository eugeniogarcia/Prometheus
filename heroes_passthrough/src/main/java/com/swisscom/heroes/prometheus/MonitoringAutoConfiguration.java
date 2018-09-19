package com.swisscom.heroes.prometheus;

import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.prometheus.client.CollectorRegistry;


@Configuration
@PropertySource("classpath:/com/swisscom/monitoring.properties")
public class MonitoringAutoConfiguration {

	@Bean
	public MetadataProperties metadataProperties() {
		return new MetadataProperties ();
	}

	@ConditionalOnMissingBean
	@Bean
	public PushGatewayProperties pushGatewayProperties() {
		return new PushGatewayProperties();
	}

	@ConditionalOnMissingBean
	@Bean
	CollectorRegistry collectorRegistry() {
		return CollectorRegistry.defaultRegistry;
	}

	@Bean
	@ConditionalOnEnabledInfoContributor("build")
	@ConditionalOnResource(resources = "classpath:/META-INF/build-info.properties")
	@ConditionalOnMissingBean(name = "metricsVersionTag")
	MeterRegistryCustomizer<MeterRegistry> metricsVersionTag(
			final BuildProperties buildProperties) {

		return registry -> registry.config()
				.meterFilter(new MeterFilter() {
					@Override
					public Meter.Id map(final Meter.Id id) {
						if (id.getName().startsWith("process.start")) {
							return id.withTag(Tag.of("version", buildProperties.getVersion()));
						}
						return id;
					}
				});
	}


	@ConditionalOnProperty(value = "management.metrics.export.prometheus.pushgateway.enabled", matchIfMissing = true)
	@Bean
	PrometheusPusher prometheusPusher(final CollectorRegistry collectorRegistry) {
		return new PrometheusPusher(collectorRegistry, pushGatewayProperties(), metadataProperties());
	}

}
