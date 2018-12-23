package io.codematch.gameofthree.game.application;

import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Immutable
@JsonDeserialize(as = ImmutableGameCreateRequest.class)
public interface GameCreateRequest {
	String getName();

	Optional<Integer> getInitialValue();
}
