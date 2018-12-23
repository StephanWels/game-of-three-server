package io.codematch.gameofthree.game.application;

import java.util.UUID;

import org.immutables.value.Value.Immutable;

@Immutable
public interface Player {
	UUID getId();

	String getName();
}
