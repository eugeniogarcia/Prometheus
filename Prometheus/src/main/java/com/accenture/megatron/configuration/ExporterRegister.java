package com.accenture.megatron.configuration;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;

@Configuration
public class ExporterRegister {
	private List<Collector> collectors; 
	private PushGateway pg=new PushGateway("localhost:9091");
	private Logger logger = LoggerFactory.getLogger(getClass());
	 
	@Autowired
	CollectorRegistry registro;
	
	@Scheduled(cron = "*/30 * * * * *")
	public void send() {
        try {
			pg.pushAdd(registro,"Servicio");
			logger.info("Envia al PG");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Error al enviar al PG");
			logger.error(e.getMessage());
			//e.printStackTrace();
		}
		
	}
	
    public ExporterRegister(List<Collector> collectors) {
         for (Collector collector : collectors) {
             collector.register();
         }
         this.collectors = collectors;
    }

    public List<Collector> getCollectors() {
         return collectors;
    }
}
