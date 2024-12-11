package com.gkuznetsov.kafkanotificationrestapi;

import org.springframework.boot.SpringApplication;

public class TestKafkanotificationrestapiApplication {

	public static void main(String[] args) {
		SpringApplication.from(KafkanotificationrestapiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
