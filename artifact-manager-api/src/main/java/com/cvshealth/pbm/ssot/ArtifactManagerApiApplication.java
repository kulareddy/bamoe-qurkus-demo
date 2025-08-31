package com.cvshealth.pbm.ssot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
public class ArtifactManagerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtifactManagerApiApplication.class, args);
    }

}
