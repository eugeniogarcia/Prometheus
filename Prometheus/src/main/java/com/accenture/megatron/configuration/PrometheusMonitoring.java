package com.accenture.megatron.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.prometheus.client.exporter.PushGateway;

@SpringBootApplication
//Para usar PrometheusGW
@EnableScheduling
@ComponentScan(basePackages = "com.accenture.megatron")
public class PrometheusMonitoring{

		public static void main(String[] args){
		new SpringApplication(PrometheusMonitoring.class).run(args);
	}

	
}