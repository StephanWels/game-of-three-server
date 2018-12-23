package io.codematch.gameofthree.player.domain

import spock.lang.Specification

class PlayerSpec extends Specification {

    def "new player is rejected, if name is offending"() {
        when:
        Player.newPlayer('xXx_porn_xXx', 'email@web.de')

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.contains('not for you')
    }

}
