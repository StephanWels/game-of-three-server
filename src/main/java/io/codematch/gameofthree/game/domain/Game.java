package io.codematch.gameofthree.game.domain;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.codematch.gameofthree.game.application.GameTurn;
import io.codematch.gameofthree.game.application.ImmutableGameTurn;
import io.codematch.gameofthree.game.application.Player;

@Immutable
@JsonSerialize(as = ImmutableGame.class)
@JsonDeserialize(as = ImmutableGame.class)
public abstract class Game {

	public abstract UUID getId();

	public abstract String getName();

	public abstract int getGameValue();

	public abstract List<GameTurn> getGameTurns();

	public abstract List<Player> getPlayers();

	public abstract Optional<Player> getWinner();

	private static int initializeGameValue() {
		return new Random().nextInt(50) + 10;
	}

	public static Game newGame(String name) {
		return newGame(name, initializeGameValue());
	}

	public static Game newGame(String name, int startGameValue) {
		validateInitialValue(startGameValue);
		return ImmutableGame.builder().id(UUID.randomUUID()).name(name).gameValue(startGameValue).build();
	}

	private static void validateInitialValue(int startGameValue) {
		if (startGameValue <= 1) {
			throw new IllegalArgumentException("%s is not a valid starting value. Has to be greate than 1");
		}
	}

	public Game takeTurn(GameTurn gameTurn) {
		validateTurnIsValidOrThrow(gameTurn);
		final int newGameValue = (getGameValue() + gameTurn.getMove()) / 3;
		final List<GameTurn> newGameTurns = Stream.concat(getGameTurns().stream(), Stream.of(gameTurn)).collect(Collectors.toList());

		final Optional<Player> winner;
		if (newGameValue == 1) {
			winner = getPlayers().stream().filter(player -> player.getId().equals(gameTurn.getPlayer().getId())).findFirst();
		} else {
			winner = Optional.empty();
		}

		final ImmutableGame gameAfterTurn = ImmutableGame.copyOf(this).withGameValue(newGameValue).withGameTurns(newGameTurns).withWinner(winner);
		return gameAfterTurn.takeAutomaticTurns();
	}

	Game takeAutomaticTurns() {
		if (gameHasEnded() || gameIsWaitingForPlayers()) {
			return this;
		}
		final Optional<Player> automaticPlayerTakingTurn = getPlayers().stream().filter(player -> isPlayerAllowedToTakeTurn(player.getId()))
				.filter(Player::isAutomaticTurns).findFirst();
		return automaticPlayerTakingTurn
				.map(player -> this.takeTurn(ImmutableGameTurn.builder().player(player).move(determineNextAutoMove()).build()).takeAutomaticTurns())
				.orElse(this);
	}

	private int determineNextAutoMove() {
		switch (getGameValue() % 3) {
		case 0:
			return 0;
		case 1:
			return -1;
		case 2:
			return 1;
		default:
			throw new IllegalStateException("AI Player failed to calculate turn");
		}
	}

	private void validateTurnIsValidOrThrow(GameTurn turn) {
		final boolean correctPlayerTakesTurn = isPlayerAllowedToTakeTurn(turn.getPlayer().getId());
		if (!correctPlayerTakesTurn) {
			throw new IllegalArgumentException("Not your turn!");
		}
		final boolean playerHasJoinedTheGame = getPlayers().stream().map(Player::getId).anyMatch(turn.getPlayer().getId()::equals);
		if (!playerHasJoinedTheGame) {
			throw new IllegalArgumentException("You havn't joined the game yet!");
		}
		if (gameHasEnded()) {
			throw new IllegalArgumentException("Game has already ended");
		}
		final boolean resultingValueIsMultipleOfThree = ((getGameValue() + turn.getMove()) % 3) == 0;
		if (!resultingValueIsMultipleOfThree) {
			throw new IllegalArgumentException("Game turn is not valid - Resulting value is not a multiple of three!");
		}
		if (gameIsWaitingForPlayers()) {
			throw new IllegalArgumentException("Two players are needed before starting the game.");
		}
	}

	private boolean gameIsWaitingForPlayers() {
		return getPlayers().size() != 2;
	}

	private boolean gameHasEnded() {
		return getGameValue() == 1;
	}

	private boolean isPlayerAllowedToTakeTurn(UUID playerId) {
		return getGameTurns().isEmpty() || !getGameTurns().get(getGameTurns().size() - 1).getPlayer().getId().equals(playerId);
	}

	public Game addPlayer(Player player) {
		validatePlayerCanJoinOrThrow(player.getId());
		final List<Player> newPlayerList = Stream.concat(getPlayers().stream(), Stream.of(player)).collect(Collectors.toList());
		return ImmutableGame.copyOf(this).withPlayers(newPlayerList).takeAutomaticTurns();
	}

	private void validatePlayerCanJoinOrThrow(UUID playerId) {
		if (getPlayers().stream().map(Player::getId).anyMatch(playerId::equals)) {
			throw new IllegalArgumentException(String.format("The player with id '%s' is already in the game.", playerId));
		}
		if (getPlayers().size() == 2) {
			throw new IllegalArgumentException("Game is full.");
		}
	}
}
