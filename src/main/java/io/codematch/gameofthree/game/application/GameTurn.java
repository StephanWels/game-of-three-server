package io.codematch.gameofthree.game.application;

import java.util.UUID;

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Immutable
@JsonDeserialize(as = ImmutableGameTurn.class)
public interface GameTurn {
	int getMove();
	UUID getPlayerId();
}
