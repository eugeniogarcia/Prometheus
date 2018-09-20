package com.accenture.megatron.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import io.prometheus.client.Gauge;

@Component
public class RequestGaugeInterceptor extends HandlerInterceptorAdapter {

    // Declara un gauge con tres etiquetas, y lo registra
	//es private static, con lo que nos aseguramos el patron singleton
	static final Gauge inprogressRequests = Gauge.build()
		.name("peticiones_en_curso")
		.labelNames("metodo", "handler", "estado")
		.help("Inprogress requests.")
		.register();

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)throws Exception {
        // Update counters
        String handlerLabel = handler.toString();
        // get short form of handler method name
        if (handler instanceof HandlerMethod) {
             Method method = ((HandlerMethod) handler).getMethod();
             handlerLabel = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        }
        //Especifica las etiquetas
        inprogressRequests.labels(request.getMethod(), handlerLabel, Integer.toString(response.getStatus())).dec();
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Update counters
        String handlerLabel = handler.toString();
        // get short form of handler method name
        if (handler instanceof HandlerMethod) {
             Method method = ((HandlerMethod) handler).getMethod();
             handlerLabel = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        }
        //Especifica las etiquetas
        inprogressRequests.labels(request.getMethod(), handlerLabel, Integer.toString(response.getStatus())).inc();
        return true;
    }

}