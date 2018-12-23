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
			winner = getPlayers().stream().filter(player -> player.getId().equals(gameTurn.getPlayerId())).findFirst();
		} else {
			winner = Optional.empty();
		}
		return ImmutableGame.copyOf(this).withGameValue(newGameValue).withGameTurns(newGameTurns).withWinner(winner);
	}

	private void validateTurnIsValidOrThrow(GameTurn turn) {
		final boolean correctPlayerTakesTurn =
				getGameTurns().isEmpty() || !getGameTurns().get(getGameTurns().size() - 1).getPlayerId().equals(turn.getPlayerId());
		if (!correctPlayerTakesTurn) {
			throw new IllegalArgumentException("Not your turn!");
		}
		final boolean playerHasJoinedTheGame = getPlayers().stream().map(Player::getId).anyMatch(turn.getPlayerId()::equals);
		if (!playerHasJoinedTheGame) {
			throw new IllegalArgumentException("You havn't joined the game yet!");
		}
		final boolean gameHasAlreadyEnded = getGameValue() == 1;
		if (gameHasAlreadyEnded) {
			throw new IllegalArgumentException("Game has already ended");
		}
		final boolean resultingValueIsMultipleOfThree = ((getGameValue() + turn.getMove()) % 3) == 0;
		if (!resultingValueIsMultipleOfThree) {
			throw new IllegalArgumentException("Game turn is not valid - Resulting value is not a multiple of three!");
		}
	}

	public Game addPlayer(Player player) {
		validatePlayerCanJoinOrThrow(player.getId());
		final List<Player> newPlayerList = Stream.concat(getPlayers().stream(), Stream.of(player)).collect(Collectors.toList());
		return ImmutableGame.copyOf(this).withPlayers(newPlayerList);
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
