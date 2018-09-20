package com.accenture.megatron.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import io.prometheus.client.Summary;

@Component
public class RequestSummaryInterceptor extends HandlerInterceptorAdapter {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String REQ_PARAM_TIMING = "timing";

    private static final Summary responseTimeInMs = Summary
         .build()
         //Podemos añadir percentiles
         //Mediana con error 5%
         .quantile(0.5, 0.05)   
         //Porcentil 90% con error 1%
         .quantile(0.9, 0.01)   
         .name("duracion_en_segundos")
         .labelNames("method", "handler", "status")
         .help("Captura cualquier medida; En este caso la duracion")
         .register();

    private static final Summary duracion= Summary
            .build()
            .name("http_duracion")
            .labelNames("method", "handler", "status")
            .help("Una forma especializada de obtener la duracion")
            .register();

    private Summary.Timer tiempo;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
         request.setAttribute(REQ_PARAM_TIMING, System.currentTimeMillis());
         return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)throws Exception {
         Long timingAttr = (Long) request.getAttribute(REQ_PARAM_TIMING);
         long completedTime = System.currentTimeMillis() - timingAttr;
         String handlerLabel = handler.toString();
         // get short form of handler method name
         if (handler instanceof HandlerMethod) {
              Method method = ((HandlerMethod) handler).getMethod();
              handlerLabel = method.getDeclaringClass().getSimpleName() + "." + method.getName();
         }
         
         responseTimeInMs
         .labels(request.getMethod(), handlerLabel, Integer.toString(response.getStatus()))
         .observe(completedTime);
    }
}