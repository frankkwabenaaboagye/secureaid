package org.frank.secureaid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SecureaidApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureaidApplication.class, args);
    }

}

@RestController
class AppTestController{

    @GetMapping("/")
    public String test(){
        return "Hello World";
    }
}