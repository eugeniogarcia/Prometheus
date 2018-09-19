package com.swisscom.heroes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.web.client.MetricsRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;



@Configuration
public class Configure {

	private static final Logger LOGGER = LoggerFactory.getLogger(Configure.class);

	@Bean
	public RestTemplateProperties restTemplateProperties() {
		return new RestTemplateProperties();
	}

	@Primary
	@Bean
	public RestTemplateBuilder oceRestTemplateBuilder(final MetricsRestTemplateCustomizer rtc, final RestTemplateProperties properties) {
		return new RestTemplateBuilder()
				.setConnectTimeout(properties.getConnectTimeout())
				.setReadTimeout(properties.getReadTimeout())

				.customizers(rtc);
	}

	@Primary
	@Bean
	public RestTemplate oceRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.build();
	}

}
