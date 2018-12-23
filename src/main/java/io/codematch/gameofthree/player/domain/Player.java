package io.codematch.gameofthree.player.domain;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayer.class)
@JsonDeserialize(as = ImmutablePlayer.class)
public abstract class Player {

	public abstract UUID getId();

	public abstract String getName();

	public abstract String getEmail();

	public static Player newPlayer(@NotNull String name, @NotNull String emailAddress) {
		rejectOffensiveUsernames(name);
		return ImmutablePlayer.builder().name(name).email(emailAddress).id(UUID.randomUUID()).build();
	}

	private static void rejectOffensiveUsernames(@NotNull String name) {
		if (name.contains("porn")){
			throw new RuntimeException(String.format("Sorry, %s - not for you", name));
		}
	}

}
