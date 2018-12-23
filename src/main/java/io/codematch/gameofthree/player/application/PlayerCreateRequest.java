package io.codematch.gameofthree.player.application;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutablePlayerCreateRequest.class)
public interface PlayerCreateRequest {
	@NotNull
	String getName();

	@NotNull
	String getEmail();
}
