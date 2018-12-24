package io.codematch.gameofthree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;

@SpringBootApplication
public class GameOfThreeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameOfThreeApplication.class, args);
	}

}

