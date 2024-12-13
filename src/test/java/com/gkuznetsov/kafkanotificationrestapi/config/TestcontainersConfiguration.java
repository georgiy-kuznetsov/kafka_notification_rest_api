package com.gkuznetsov.kafkanotificationrestapi.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@Profile("test")
public class TestcontainersConfiguration {

	@Bean
	@Qualifier("testKafkaContainer")
	public KafkaContainer kafkaContainer() {
		DockerImageName kafkaImage = DockerImageName
				.parse("apache/kafka:latest");

		KafkaContainer kafkaContainer = new KafkaContainer(kafkaImage).withExposedPorts(9092);

		return kafkaContainer;
	}

	@Bean
	public String kafkaBootstrapServers(KafkaContainer kafkaContainer) {
		kafkaContainer.start();
		return kafkaContainer.getBootstrapServers();
	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	}
}
