package io.codematch.gameofthree.player.application;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.codematch.gameofthree.player.domain.Player;
import io.codematch.gameofthree.player.domain.PlayerRepository;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

	private final PlayerRepository playerRepository;

	public PlayerController(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}

	@GetMapping()
	public List<Player> findAll() {
		return playerRepository.findAll();
	}

	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public Player createPlayer(@RequestBody @Valid PlayerCreateRequest playerCreateRequest) {
		return playerRepository.save(Player.newPlayer(playerCreateRequest.getName(), playerCreateRequest.getEmail()));
	}
}
