package io.codematch.gameofthree.game.infrastructure;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface GameKafkaBinding {

	String INPUT = "gameInput";
	String OUTPUT = "gameOutput";

	@Input
	SubscribableChannel gameInput();

	@Output
	MessageChannel gameOutput();


}
