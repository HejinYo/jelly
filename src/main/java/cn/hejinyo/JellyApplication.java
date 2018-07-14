package cn.hejinyo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JellyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JellyApplication.class, args);
    }
}
