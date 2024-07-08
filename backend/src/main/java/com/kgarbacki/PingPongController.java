package com.kgarbacki;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingPongController {

    record PingPong(String result){}
    private static int COUNTER = 0;
    @GetMapping("/ping")
    public PingPong getPingPong(){
        return new PingPong("Pong " + ++COUNTER);
    }
}
