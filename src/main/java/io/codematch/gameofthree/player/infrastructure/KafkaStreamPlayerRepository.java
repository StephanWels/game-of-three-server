package io.codematch.gameofthree.player.infrastructure;

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

import io.codematch.gameofthree.player.domain.Player;
import io.codematch.gameofthree.player.domain.PlayerRepository;

@Component
@EnableBinding(PlayerKafkaBinding.class)
public class KafkaStreamPlayerRepository implements PlayerRepository {

	private final HashMap<UUID, Player> playerStore = new HashMap<>();

	private final MessageChannel messageChannel;

	public KafkaStreamPlayerRepository(@Qualifier(PlayerKafkaBinding.OUTPUT) MessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}

	@StreamListener(PlayerKafkaBinding.INPUT)
	public void handle(Player player) {
		this.playerStore.put(player.getId(), player);
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
		this.messageChannel.send(new MutableMessage<Player>(player));
		return player;
	}
}
