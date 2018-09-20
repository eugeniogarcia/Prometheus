package com.accenture.megatron.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import io.prometheus.client.Counter;

@Component
public class RequestCounterInterceptor extends HandlerInterceptorAdapter {

    // Declara un contador con tres etiquetas, y lo registra
	//es private static, con lo que nos aseguramos el patron singleton
    private static final Counter requestTotal = Counter.build()
    		//especifica el nombre
    		.name("Total_peticiones")
    		//etiquetas
    		.labelNames("metodo", "handler", "estado")
    		//y la ayuda
    		.help("Total peticiones http")
    		.register();

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
throws Exception {
         // Update counters
         String handlerLabel = handler.toString();
         // get short form of handler method name
         if (handler instanceof HandlerMethod) {
              Method method = ((HandlerMethod) handler).getMethod();
              handlerLabel = method.getDeclaringClass().getSimpleName() + "." + method.getName();
         }
         //Especifica las etiquetas
         requestTotal.labels(request.getMethod(), handlerLabel, Integer.toString(response.getStatus())).inc();
    }
}