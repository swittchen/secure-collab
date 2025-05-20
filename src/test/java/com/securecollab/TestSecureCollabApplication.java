package com.securecollab;

import org.springframework.boot.SpringApplication;

public class TestSecureCollabApplication {

	public static void main(String[] args) {
		SpringApplication.from(SecureCollabApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
