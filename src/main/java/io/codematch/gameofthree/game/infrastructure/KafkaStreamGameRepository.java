package io.codematch.gameofthree.game.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.codematch.gameofthree.game.domain.Game;
import io.codematch.gameofthree.game.domain.GameRepository;

@Component
public class KafkaStreamGameRepository implements GameRepository {

	HashMap<UUID, Game> gameStore = new HashMap<>();

	public KafkaStreamGameRepository() {
		Stream.of(Game.newGame("First Game", 422), Game.newGame("Second Game", 18)).forEach(game -> gameStore.put(game.getId(), game));
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
		this.gameStore.put(game.getId(), game);
		return game;
	}
}
