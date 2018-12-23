package io.codematch.gameofthree.game.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository {
	List<Game> findAll();
	Optional<Game> findOne(UUID gameId);
	Game save(Game game);
}
