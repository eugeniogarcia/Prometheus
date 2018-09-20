package com.accenture.megatron.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import io.prometheus.client.Histogram;

@Component
public class RequestHistogramInterceptor extends HandlerInterceptorAdapter {

	 private static final String REQ_PARAM_TIMING = "timing";
	
	 static final Histogram requestLatency = Histogram.build()
	     .name("latencia_en_segundos")
	     .labelNames("metodo", "handler", "estado")
	     .help("Request latency in seconds.")
	     .register();


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
	         requestLatency
	         .labels(request.getMethod(), handlerLabel, Integer.toString(response.getStatus()))
	         .observe(completedTime);
	    }
}