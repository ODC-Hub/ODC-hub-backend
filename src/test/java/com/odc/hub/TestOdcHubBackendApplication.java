package com.odc.hub;

import org.springframework.boot.SpringApplication;

public class TestOdcHubBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(OdcHubBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
