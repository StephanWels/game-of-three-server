package io.codematch.gameofthree.game.domain

import io.codematch.gameofthree.game.application.GameTurn
import io.codematch.gameofthree.game.application.ImmutableGameTurn
import io.codematch.gameofthree.game.application.ImmutablePlayer
import io.codematch.gameofthree.game.application.Player
import spock.lang.Specification

class GameTakeTurnSpec extends Specification {

    Player player1 = ImmutablePlayer.builder().name('Player 1').id(UUID.randomUUID()).build()
    Player player2 = ImmutablePlayer.builder().name('Player 2').id(UUID.randomUUID()).build()
    Player player3 = ImmutablePlayer.builder().name('Player 3').id(UUID.randomUUID()).build()
    Game newGameWithTwoPlayers = Game.newGame('Game name', 6).addPlayer(player1).addPlayer(player2)

    def 'take turn appends a new turn and updates the game value if legit'() {
        given:
        GameTurn gameTurn = ImmutableGameTurn.builder().move(0).player(player1).build()

        when:
        Game game = newGameWithTwoPlayers.takeTurn(gameTurn)

        then:
        game.gameTurns == [gameTurn]
        game.gameValue == 2
    }

    def 'take turn sets the winner, when the game value reaches 1'() {
        given:
        GameTurn firstGameTurn = ImmutableGameTurn.builder().move(0).player(player1).build()
        GameTurn secondGameTurn = ImmutableGameTurn.builder().move(1).player(player2).build()

        when:
        Game finishedGame = newGameWithTwoPlayers.takeTurn(firstGameTurn).takeTurn(secondGameTurn)

        then:
        finishedGame.gameValue == 1;
        finishedGame.winner.get() == player2;
    }

    def 'take turn is rejected if the resulting value is not a multiple of 3'() {
        given: 'an invalid game turn'
        GameTurn gameTurn = ImmutableGameTurn.builder().move(badMove).player(player1).build()

        when:
        newGameWithTwoPlayers.takeTurn(gameTurn)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.contains('not a multiple of three!')

        where:
        badMove << [-1, 1]
    }

    def 'take turn is rejected if the turn is made by a player who did not join the game'() {
        given: 'a game, where there is already two players'
        GameTurn gameTurn = ImmutableGameTurn.builder().move(0).player(player3).build()

        when: 'the third player tries to take his turn'
        newGameWithTwoPlayers.takeTurn(gameTurn)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'You havn\'t joined the game yet!'
    }

    def 'take turn is rejected if a player tries to make two turns in row'() {
        when: 'the third player tries to take his turn'
        newGameWithTwoPlayers.takeTurn(ImmutableGameTurn.builder().move(0).player(player1).build())
                .takeTurn(ImmutableGameTurn.builder().move(1).player(player1).build())

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Not your turn!'
    }

    def 'take turn is rejected if the game has already ended'() {
        given: 'a game that has already ended'
        Game game = ImmutableGame.builder().id(UUID.randomUUID()).name('a finished game').gameValue(1).players([player1, player2]).build();

        when: 'the third player tries to take his turn'
        game.takeTurn(ImmutableGameTurn.builder().move(0).player(player1).build())

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Game has already ended'
    }

    def 'cannot take turn before a exactly two players joined the game'() {
        given: 'a game, where there is only one player'
        Game game = Game.newGame('Game name', 6).addPlayer(player1)

        when:
        game.takeTurn(ImmutableGameTurn.builder().move(0).player(player1).build())

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Two players are needed before starting the game.'
    }

    def 'Turns for players with automatic turns option activated will be determined by the game itself'() {
        given: 'a game, with one manual and one automatic player'
        Player autoPlayer = ImmutablePlayer.builder().name('Auto player').id(UUID.randomUUID()).isAutomaticTurns(true).build()
        Game game = Game.newGame('Game name', 18).addPlayer(player1)

        when:
        Game gameAfterSecondPlayerJoined = game.addPlayer(autoPlayer)

        then:
        gameAfterSecondPlayerJoined.gameTurns.size() == 1

        when:
        Game gameAfterManualMove = gameAfterSecondPlayerJoined.takeTurn(ImmutableGameTurn.builder().player(player1).move(0).build())

        then:
        gameAfterManualMove.gameTurns.size() == 3
        gameAfterManualMove.winner.get() == autoPlayer;
    }
}
