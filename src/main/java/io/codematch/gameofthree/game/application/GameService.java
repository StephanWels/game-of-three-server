package io.codematch.gameofthree.game.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.codematch.gameofthree.game.domain.Game;
import io.codematch.gameofthree.game.domain.GameRepository;

@Service
public class GameService {

	private final GameRepository gameRepository;

	public GameService(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}

	public Game takeTurn(UUID gameId, GameTurn gameTurn) {
		final Game game = findGame(gameId);
		return gameRepository.save(game.takeTurn(gameTurn));
	}

	public Game joinGame(UUID gameId, Player player) {
		final Game game = findGame(gameId);
		return gameRepository.save(game.addPlayer(player));
	}

	public Game findGame(UUID gameId) {
		return gameRepository.findOne(gameId).orElseThrow(() -> new RuntimeException(String.format("Game with id '%s' could not be found", gameId)));
	}
}
