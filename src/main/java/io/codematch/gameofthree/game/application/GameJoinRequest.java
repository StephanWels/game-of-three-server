package io.codematch.gameofthree.game.application;

import java.util.UUID;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Immutable
@JsonDeserialize(as = ImmutableGameJoinRequest.class)
public interface GameJoinRequest {
	UUID getPlayerId();
	String getPlayerName();
}
