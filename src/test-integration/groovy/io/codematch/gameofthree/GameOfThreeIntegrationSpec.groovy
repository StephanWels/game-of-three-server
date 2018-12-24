package io.codematch.gameofthree

import io.codematch.gameofthree.game.application.ImmutableGameCreateRequest
import io.codematch.gameofthree.game.application.ImmutableGameJoinRequest
import io.codematch.gameofthree.game.application.ImmutableGameTurn
import io.codematch.gameofthree.game.application.ImmutablePlayer
import io.codematch.gameofthree.game.domain.Game
import io.codematch.gameofthree.game.infrastructure.GameKafkaBinding
import io.codematch.gameofthree.player.application.ImmutablePlayerCreateRequest
import io.codematch.gameofthree.player.domain.Player
import io.codematch.gameofthree.player.infrastructure.PlayerKafkaBinding
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import java.util.stream.Collectors

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameOfThreeIntegrationSpec extends Specification {

    TestRestTemplate restTemplate = new TestRestTemplate()

    @Autowired
    Environment environment;

    @Autowired
    private GameKafkaBinding gameBinding;

    @Autowired
    private PlayerKafkaBinding playerBinding;

    @Autowired
    private MessageCollector messageCollector;


    @Before
    def 'setup http client'() {
        restTemplate.setUriTemplateHandler(new LocalHostUriTemplateHandler(environment))
    }

    def 'create game with two players (one of them auto) and finish'() {
        when: 'we query the game database for the first time'
        ResponseEntity<List<Game>> listGames = restTemplate.exchange("/api/games", HttpMethod.GET, null, new ParameterizedTypeReference<List<Game>>() {
        })

        then: 'there should be no games'
        listGames.statusCode == HttpStatus.OK
        listGames.body.isEmpty()

        when: 'we create a game and query the database again'
        ResponseEntity<Game> newGame = restTemplate.postForEntity("/api/games", ImmutableGameCreateRequest.builder().name('Game of Three').initialValue(16).build(), Game.class)
        flushMessages();
        listGames = restTemplate.exchange("/api/games", HttpMethod.GET, null, new ParameterizedTypeReference<List<Game>>() {})

        then: 'a game is available with no players or turns'
        newGame.statusCode == HttpStatus.CREATED
        listGames.body.size() == 1
        listGames.body[0].name == 'Game of Three'
        listGames.body[0].players == []
        listGames.body[0].gameTurns == []

        when: 'we add two playes'
        ResponseEntity<Player> player1 = restTemplate.postForEntity("/api/players", ImmutablePlayerCreateRequest.builder().name('Alice').email('alice@gmail.com').build(), Player.class)
        ResponseEntity<Player> player2 = restTemplate.postForEntity("/api/players", ImmutablePlayerCreateRequest.builder().name('Bob').email('bob@gmail.com').build(), Player.class)
        flushMessages()
        ResponseEntity<List<Player>> listPlayers = restTemplate.exchange("/api/players", HttpMethod.GET, null, new ParameterizedTypeReference<List<Player>>() {
        })

        then: 'two players - alice and bob - are available'
        player1.statusCode == HttpStatus.CREATED
        player2.statusCode == HttpStatus.CREATED
        listPlayers.body.size() == 2
        listPlayers.body.stream().map({ player -> player.name }).collect(Collectors.toSet()) == ['Alice', 'Bob'] as Set

        def alice = ImmutablePlayer.builder().name(player1.body.name).id(player1.body.id).isAutomaticTurns(true).build()
        def bob = ImmutablePlayer.builder().name(player2.body.name).id(player2.body.id).build()

        when: 'both join the game (Alice as auto, Bob as manual player)'
        restTemplate.put("/api/games/${newGame.body.id}/players", ImmutableGameJoinRequest.builder().playerId(alice.id).playerName(alice.name).isAutomaticTurns(alice.automaticTurns).build())
        flushMessages()
        restTemplate.put("/api/games/${newGame.body.id}/players", ImmutableGameJoinRequest.builder().playerId(bob.id).playerName(bob.name).isAutomaticTurns(bob.automaticTurns).build())
        flushMessages()

        then: 'Alice(auto player) will have taken her first turn (-1)'
        ResponseEntity<Game> game = restTemplate.getForEntity("/api/games/${newGame.body.id}", Game.class)
        game.body.gameTurns == [ImmutableGameTurn.builder().player(alice).move(-1).build()]

        and: 'the game value/winner is updated'
        !game.body.winner.isPresent()
        game.body.gameValue == 5

        when: 'Bob takes his first turn (+1)'
        restTemplate.put("/api/games/${newGame.body.id}/turns", ImmutableGameTurn.builder().move(+1).player(bob).build())
        flushMessages()
        game = restTemplate.getForEntity("/api/games/${newGame.body.id}", Game.class)

        then: 'Alice(auto player) takes her second turn (+1) automatically'
        game.body.gameTurns == [
                ImmutableGameTurn.builder().player(alice).move(-1).build(),
                ImmutableGameTurn.builder().player(bob).move(+1).build(),
                ImmutableGameTurn.builder().player(alice).move(+1).build(),
        ]

        and: 'alice wins the game'
        game.body.winner.get() == alice
        game.body.gameValue == 1
    }

    private flushMessages() {
        messageCollector.forChannel(gameBinding.gameOutput()).stream().forEach({ message ->
            gameBinding.gameInput().send(message)
        });
        messageCollector.forChannel(playerBinding.playerOutput()).stream().forEach({ message ->
            playerBinding.playerInput().send(message)
        });
    }
}
