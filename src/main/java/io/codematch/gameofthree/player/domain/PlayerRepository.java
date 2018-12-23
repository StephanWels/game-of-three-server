package io.codematch.gameofthree.player.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.codematch.gameofthree.game.domain.Game;

public interface PlayerRepository {
	List<Player> findAll();
	Optional<Player> findOne(UUID playerId);
	Player save(Player player);
}
