package io.codematch.gameofthree.game.application;

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Immutable
@JsonDeserialize(as = ImmutableGameTurn.class)
@JsonSerialize(as = ImmutableGameTurn.class)
public interface GameTurn {
	int getMove();

	Player getPlayer();
}
