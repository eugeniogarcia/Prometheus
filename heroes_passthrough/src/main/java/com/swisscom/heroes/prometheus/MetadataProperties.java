package com.swisscom.heroes.prometheus;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "metadata")
public class MetadataProperties {

	private String entorno = "entorno";
	private String instancia = "0";
	private String equipo = "equipo";

	public String getEntorno() {
		return entorno;
	}

	public void setEntorno(final String valor) {
		this.entorno = valor;
	}

	public String getInstancia() {
		return instancia;
	}

	public void setInstancia(final String valor) {
		this.instancia = valor;
	}

	public String getEquipo() {
		return equipo;
	}

	public void setEquipo(final String valor) {
		this.equipo = valor;
	}

}