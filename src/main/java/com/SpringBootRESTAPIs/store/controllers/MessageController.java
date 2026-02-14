package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // with rest controller we can send raw data ( or json data: a java object which will be converted to a json data)
public class MessageController {

    @RequestMapping("/hello")
    public Message sayHello() { // Message java object will be converted to json object
        return new Message("Hello World!");
    }
}
