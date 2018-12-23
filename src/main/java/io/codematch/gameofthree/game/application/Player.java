package io.codematch.gameofthree.game.application;

import java.util.UUID;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Immutable
@JsonDeserialize(as = ImmutablePlayer.class)
@JsonSerialize(as = ImmutablePlayer.class)
public interface Player {
	UUID getId();

	String getName();

	@Default()
	default boolean isAutomaticTurns(){
		return false;
	}
}
