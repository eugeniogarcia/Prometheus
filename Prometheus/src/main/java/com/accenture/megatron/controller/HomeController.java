package com.accenture.megatron.controller;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
     private Logger logger = LoggerFactory.getLogger(getClass());
 
     @RequestMapping("/endpointA")
     @ResponseBody
     public ResponseEntity<String>  handlerA() throws InterruptedException {
    	 logger.info("empieza A");
    	 ThreadLocalRandom.current().nextInt(0, 100);
          logger.info("/endpointA");
          
          return new ResponseEntity<String>("success", HttpStatus.OK);
     }
 
     @RequestMapping("/endpointB")
     @ResponseBody
     public ResponseEntity<String> handlerB() throws InterruptedException {
    	 logger.info("empieza B");
    	 Thread.sleep(ThreadLocalRandom.current().nextInt(0, 5000));
    	 logger.info("/endpointB");
    	 return new ResponseEntity<String>("success", HttpStatus.OK);
     }
}
