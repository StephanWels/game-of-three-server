package io.codematch.gameofthree.player.infrastructure;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface PlayerKafkaBinding {

	String INPUT = "playerInput";
	String OUTPUT = "playerOutput";

	@Input
	SubscribableChannel playerInput();

	@Output
	MessageChannel playerOutput();


}
