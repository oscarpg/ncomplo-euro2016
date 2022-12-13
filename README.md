# ncomplo-euro2016

Web project to create and manage bet leagues for any competition.

# Instructions

## Create a Competition

![create_competition](https://user-images.githubusercontent.com/5988819/207457602-300b4120-b5e0-482b-8ad8-81dd81bf1c92.png)

## Create the bet types

The bet types define the way that the points will be calculated

![create_competition](https://user-images.githubusercontent.com/5988819/207457823-128288b0-ed46-484e-8712-7812f5dbbf61.png)

### Side matters
    
The bet will assign points depending on the team that pass to the next round, regardless the match itself.
    
### Score matters
    
    The bet will assign points depending on the exact score of the match.
    
### Result matters
    
    The bet will assign points depending on the teams that wins the game.
    
### Example. In a World Cup event, the following bet types are usefull:

* Group stage
    
Score matters and result matters.
    
Specification:
```
if(game.scoreA && game.scoreB) {
    if(bet.scoreA==game.scoreA && bet.scoreB==game.scoreB) {
        result.winLevel=2;
        result.points = 5;
    } else {
        if(bet.scoreA>bet.scoreB && game.scoreA>game.scoreB) {
            result.winLevel=1;
            result.points = 2;
        } else {
            if(bet.scoreA<bet.scoreB && game.scoreA<game.scoreB) {
                result.winLevel=1;
                result.points = 2;
            } else {
                if(bet.scoreA==bet.scoreB && game.scoreA==game.scoreB) {
                    result.winLevel=1;
                    result.points = 2;
                } else {
                    result.winLevel=0;
                    result.points = 0;    
                }
            }
        }
    }   
}
```

* round of 16, quarter finals and semi finals

Side matters (we only want to know if a team success in that round)

Specification:
```
var totalPoints=0;
var foundA = false;
var foundB = false;
var existsGamesInRound = false;
for (var i=0;i<allGameSidesInRound.size();i++) {
    var gameSide = allGameSidesInRound.get(i);
    if (gameSide) {
        existsGamesInRound = true;
        if (bet.gameSideA != null && bet.gameSideA.name == gameSide.name) {
            foundA=true;
            totalPoints += 5; //5 in round of 16, 15 in quarter finals and 25 in semi finals
        }
        if (bet.gameSideB != null && bet.gameSideB.name == gameSide.name) {
            foundB = true;
            totalPoints += 5; //5 in round of 16, 15 in quarter finals and 25 in semi finals
        }
    }
}
if (existsGamesInRound) {
    result.points = totalPoints;
    if(foundA) {
        result.sideAWinLevel = 2;
    } else {
        result.sideAWinLevel = 0;
    }    
    if(foundB) {
        result.sideBWinLevel = 2;
    } else {
        result.sideBWinLevel = 0;
    }
}
```

* Final
Side matters and result matters (we want to give points if the user guess the teams that reach the final, and more points when the user guess the winner.

Specification:
```
var totalPoints=0;
var foundA = false;
var foundB = false;
var existsGamesInRound = false;
for (var i=0;i<allGameSidesInRound.size();i++) {
    var gameSide = allGameSidesInRound.get(i);
    if (gameSide) {
        existsGamesInRound = true;
        if (bet.gameSideA != null && bet.gameSideA.name == gameSide.name) {
            foundA=true;
            totalPoints += 35;
        }
        if (bet.gameSideB != null && bet.gameSideB.name == gameSide.name) {
            foundB = true;
            totalPoints += 35;
        }
    }
}
if ((game.scoreA > game.scoreB) && (result.betWinner != null) && (game.gameSideA.name == result.betWinner.name)) {
    totalPoints += 50;
} 
if ((game.scoreB > game.scoreA) && (result.betWinner != null) && (game.gameSideB.name == result.betWinner.name)) {
    totalPoints += 50;
} 
if (existsGamesInRound) {
    result.points = totalPoints;
    if(foundA) {
        result.sideAWinLevel = 2;
    } else {
        result.sideAWinLevel = 0;
    }    
    if(foundB) {
        result.sideBWinLevel = 2;
    } else {
        result.sideBWinLevel = 0;
    }
}
```

## Manage the rounds

## Create the team players

## Create the games

The group games are known before the competition starts. The playoff games will be updated with the teams as the competition goes on, but the games must be created before to be able to ask the bets to the users.

## Create the league

Once the competition is ready, multiple leagues can be created to that competition. Each league can define the bet type for each game in the league. This is usefull when you want to create different leagues with different rules.

## Invite people to join the league.

Once the league is created, the people will be able to send their bets before the league deadline. As the games finish, the administrators must update the game results and playoff teams to recalculate the scoreboards.

