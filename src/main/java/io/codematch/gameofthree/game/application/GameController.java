package io.codematch.gameofthree.game.application;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.codematch.gameofthree.game.domain.Game;
import io.codematch.gameofthree.game.domain.GameRepository;

@RestController
@RequestMapping("/api/games")
public class GameController {

	private final GameRepository gameRepository;
	private final GameService gameService;

	public GameController(GameRepository gameRepository, GameService gameService) {
		this.gameRepository = gameRepository;
		this.gameService = gameService;
	}

	@GetMapping
	public List<Game> findAll() {
		return gameRepository.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Game createGame(@RequestBody GameCreateRequest gameCreateRequest) {
		final Game newGame = gameCreateRequest.getInitialValue().map(initialValue -> Game.newGame(gameCreateRequest.getName(), initialValue))
				.orElseGet(() -> Game.newGame(gameCreateRequest.getName()));
		return gameRepository.save(newGame);
	}

	@GetMapping("/{gameId}")
	@ResponseStatus(HttpStatus.OK)
	public Game gameDetails(@PathVariable("gameId") UUID gameId) {
		return this.gameService.findGame(gameId);
	}

	@PutMapping("/{gameId}/players")
	@ResponseStatus(HttpStatus.OK)
	public Game joinGame(@PathVariable("gameId") UUID gameId, @RequestBody GameJoinRequest gameJoinRequest) {
		final ImmutablePlayer joiningPlayer = ImmutablePlayer.builder().id(gameJoinRequest.getPlayerId()).name(gameJoinRequest.getPlayerName()).isAutomaticTurns(gameJoinRequest.isAutomaticTurns())
				.build();
		return this.gameService.joinGame(gameId, joiningPlayer);
	}

	@PutMapping("/{gameId}/turns")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Game> takeTurn(@PathVariable UUID gameId, @RequestBody GameTurn gameTurn) {
		return ResponseEntity.ok(gameService.takeTurn(gameId, gameTurn));
	}
}
