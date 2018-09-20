package com.accenture.megatron.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.accenture.megatron.controller.RequestCounterInterceptor;
import com.accenture.megatron.controller.RequestGaugeInterceptor;
import com.accenture.megatron.controller.RequestHistogramInterceptor;
import com.accenture.megatron.controller.RequestSummaryInterceptor;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	RequestCounterInterceptor requestCounterInterceptor;
	
	@Autowired
	RequestSummaryInterceptor requestSummaryInterceptor;

	@Autowired
	RequestHistogramInterceptor requestHistogramInterceptor;
	
	@Autowired
	RequestGaugeInterceptor requestGaugeInterceptor;
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestCounterInterceptor);
		registry.addInterceptor(requestGaugeInterceptor);
		registry.addInterceptor(requestHistogramInterceptor);
		registry.addInterceptor(requestSummaryInterceptor);
	}
}