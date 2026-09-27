package dev.knmrs.ecdemo;

import org.springframework.boot.SpringApplication;

public class TestEcDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(EcDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
