package io.codematch.gameofthree.game.domain

import io.codematch.gameofthree.game.application.GameTurn
import io.codematch.gameofthree.game.application.ImmutableGameTurn
import io.codematch.gameofthree.game.application.ImmutablePlayer
import io.codematch.gameofthree.game.application.Player
import spock.lang.Specification
import spock.lang.Unroll

class GameSpec extends Specification {


    def "game should be initialized with no players, no turns and correct name"() {
        given:
        def gameName = 'Game name'

        when:
        Game game = Game.newGame(gameName)

        then:
        game.gameTurns.isEmpty()
        game.players.isEmpty()
        game.name == gameName
    }

    def 'game should be initialized with given value'() {
        expect:
        Game.newGame('Game name', 1337).gameValue == 1337
    }

    def 'game should be initialized randomly if no starting value is given'() {
        given:
        def initialGameValue = Game.newGame('Game name').gameValue

        expect:
        initialGameValue >= 10
        initialGameValue < 60
    }

    @Unroll
    def 'illegal starting value #givenStartingValue should be rejected'() {
        when:
        Game.newGame('Game name', givenStartingValue)

        then:
        thrown(IllegalArgumentException)

        where:
        givenStartingValue << [1, 0, -1];
    }

    def 'addPlayer adds the given player to the list of active players if legit'() {
        given:
        Player player1 = ImmutablePlayer.builder().name('Player 1').id(UUID.randomUUID()).build()
        Player player2 = ImmutablePlayer.builder().name('Player 2').id(UUID.randomUUID()).build()

        when: 'the first player joins'
        Game game = Game.newGame('Empty game').addPlayer(player1);

        then: 'one player is in the game'
        game.players == [player1]

        when: 'the second player joins'
        game = game.addPlayer(player2)

        then: 'two players are in the game'
        game.players == [player1, player2]
    }

    def 'addPlayer rejects the given player if the player is already in the game'(){
        given: 'a game, where there is already one player'
        Player player1 = ImmutablePlayer.builder().name('Player 1').id(UUID.randomUUID()).build()
        Game game = Game.newGame('Game name').addPlayer(player1)

        when: 'the same player tries to join again'
        game.addPlayer(player1)

        then:
        thrown(IllegalArgumentException)
    }
}
