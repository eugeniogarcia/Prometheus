package com.accenture.megatron.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import io.prometheus.client.hotspot.VersionInfoExports;

@Configuration
@ConditionalOnClass(CollectorRegistry.class)
public class PrometheusConfiguration {

	//Declara el registro de metricas en caso de que no se haya declarado ya
     @Bean
     @ConditionalOnMissingBean
     CollectorRegistry metricRegistry() {
         return CollectorRegistry.defaultRegistry;
     }

     //Configura un servlet para que prometheus escrape las metricas
     @Bean
     ServletRegistrationBean registerPrometheusExporterServlet(CollectorRegistry metricRegistry) {
           return new ServletRegistrationBean(new MetricsServlet(metricRegistry), "/prometheus");
     }
     
     //Declara una Bean que se encargara de registrar los collectors por defeto
     @Bean
     ExporterRegister exporterRegister() {
    	 //Declara los colectores que vamos a capturar
    	 List<Collector> collectors = new ArrayList<Collector>();
    	 //Especifica las metricas a exportar
    	 collectors.add(new StandardExports());
		 collectors.add(new MemoryPoolsExports());
		 collectors.add(new VersionInfoExports());
		 collectors.add(new ThreadExports());
		 //Los registra 
         ExporterRegister register = new ExporterRegister(collectors);
         return register;
      }
}