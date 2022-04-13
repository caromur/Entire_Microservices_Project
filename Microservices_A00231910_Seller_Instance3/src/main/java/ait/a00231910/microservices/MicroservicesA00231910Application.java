package ait.a00231910.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroservicesA00231910Application {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicesA00231910Application.class, args);
	}

}
