package io.codematch.gameofthree.player.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.codematch.gameofthree.player.domain.Player;
import io.codematch.gameofthree.player.domain.PlayerRepository;

@Component
public class KafkaStreamPlayerRepository implements PlayerRepository {

	HashMap<UUID, Player> playerStore = new HashMap<>();

	public KafkaStreamPlayerRepository() {
		Stream.of(Player.newPlayer("Stephan", "stephan.wels@code-match.io"), Player.newPlayer("Sandra", "sandra.wels@code-match.io"))
				.forEach(player -> playerStore.put(player.getId(), player));
	}

	@Override
	public List<Player> findAll() {
		return new ArrayList<>(playerStore.values());
	}

	@Override
	public Optional<Player> findOne(UUID playerId) {
		return Optional.ofNullable(playerStore.get(playerId));
	}

	@Override
	public Player save(Player player) {
		playerStore.put(player.getId(), player);
		return player;
	}
}
