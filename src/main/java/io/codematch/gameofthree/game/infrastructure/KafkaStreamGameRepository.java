package io.codematch.gameofthree.game.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MutableMessage;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import io.codematch.gameofthree.game.domain.Game;
import io.codematch.gameofthree.game.domain.GameRepository;

@Component
@EnableBinding(GameKafkaBinding.class)
public class KafkaStreamGameRepository implements GameRepository {

	private final MessageChannel messageChannel;

	private final HashMap<UUID, Game> gameStore = new HashMap<>();

	public KafkaStreamGameRepository(@Qualifier(GameKafkaBinding.OUTPUT) MessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}

	@StreamListener(GameKafkaBinding.INPUT)
	public void handle(Game game) {
		this.gameStore.put(game.getId(), game);
	}

	@Override
	public List<Game> findAll() {
		return new ArrayList<>(gameStore.values());
	}

	@Override
	public Optional<Game> findOne(UUID gameId) {
		return Optional.ofNullable(gameStore.get(gameId));
	}

	@Override
	public Game save(Game game) {
		this.messageChannel.send(new MutableMessage<Game>(game));
		return game;
	}

}
