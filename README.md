# game-of-three-server

The game is designed to run as a set of docker services:
1. Server (The backend providing a RESTful API to create games/players and take turns) - source code is hosted in this repository
2. Client - An Angular frontend connected to the server REST API. Source Code can be found [here](https://github.com/StephanWels/game-of-three-server/edit/master/README.md) 
3. Kafka Broker - used for (very basic) event sourcing. (since eventing counts as a bonus :D)
4. Zookeeper - needed by Kafka

## HOW-TO start
only preqequisite: docker / docker-compose

Simply use the `docker-compose.yml` to spin up the game:
```bash
docker-compose up
```

Make sure the ports `8033` (used by nginx for delivering the frontend) and `8080` (backend server) are free.

## HOW-TO play
Use two Browser windows as clients and visit localhost:8033.

1. Login to the game. (Creates Player in Backend)
2. Create a new Game. (Creates Game in Backend)
3. Join Game with both players - if you want a player to take automatic turns, choose (JOIN AUTO)
4. Take turns until a player wins.
5. ...
6. profit
