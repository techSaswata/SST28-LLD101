# Snake & Ladder — My Notes (LLD: Snake and Ladder Game)

---

## What the problem asks to do

Design a Snake & Ladder game with:
1. Configurable board size (n x n)
2. Multiple players support
3. Difficulty levels (easy = more ladders, hard = more snakes)
4. Random snake and ladder placement
5. Turn-based gameplay
6. Automatic win detection

---

## The concepts being used

### SRP + Strategy Pattern + Builder Pattern

Think of a real board game night:
- **The board** only knows its layout — where the snakes and ladders are. It doesn't roll dice or track whose turn it is.
- **The dice** only rolls. It doesn't know about snakes or players.
- **The game** coordinates turns. It asks the dice to roll, asks the board what happens at that cell, moves the player, checks for a winner.
- **The builder** lets you configure everything (board size, difficulty, players) before the game starts — like setting up the box before you play.

**SRP**: Board, Dice, Game, Player — each has one job.
**OCP**: Want a different way to generate snakes/ladders? Create a new `BoardEntityGenerator`. Don't touch `Board` or `Game`.
**DIP**: `GameBuilder` depends on the `BoardEntityGenerator` interface, not on `RandomBoardEntityGenerator` directly.

---

## Steps to identify, understand what to do, and what is exactly done to solve it

**Step 1 — Model the board entities (snakes and ladders)**

A snake or ladder is just a teleporter: you land on cell X, you get moved to cell Y. If Y < X it's a snake (you go down). If Y > X it's a ladder (you go up).

We created `BoardEntity` with `start`, `end`, and `type` (either `"SNAKE"` or `"LADDER"`). Simple immutable data — the entity doesn't do anything, it just describes where the teleport goes.

**Step 2 — Build the Board**

The board knows its size and where the entities are. When a player lands on a cell, the board tells you the final position (same cell if nothing's there, or the entity's end if there's a snake/ladder).

We created `Board` with `size`, `totalCells` (size * size), and a `HashMap<Integer, BoardEntity>` mapping cell numbers to entities. The key method is `getNextPosition(int position)` — it looks up the cell in the map, and if there's an entity, prints "Hit SNAKE/LADDER" and returns the new position.

**Step 3 — Extract board generation behind an interface**

How snakes and ladders are placed could vary — maybe random, maybe from a config file, maybe hand-crafted levels. This is a reason to change that shouldn't affect the Board.

We created `BoardEntityGenerator` interface with `generate(int boardSize)`. `RandomBoardEntityGenerator` implements it — it takes `snakeCount` and `ladderCount`, randomly places them on the board avoiding cell 1 and the last cell, and avoids duplicates using a `HashSet` of used cells. Snakes always go downward (end < start), ladders always go upward (end > start).

**Step 4 — Add difficulty levels**

Easy = fewer snakes, more ladders. Hard = more snakes, fewer ladders. This is just configuration data.

We created `Difficulty` enum with `EASY(4 snakes, 6 ladders)` and `HARD(6 snakes, 4 ladders)`. The `GameBuilder` reads these counts and passes them to the `RandomBoardEntityGenerator`.

**Step 5 — Build the Game engine**

The game loops through players, rolls the dice, moves the player, checks the board for snakes/ladders, and checks for a winner. If a roll would take a player past the last cell, the move is skipped.

We created `Game` with `Board`, `Dice`, `List<Player>`, `finished` flag, and `winner`. The `play()` method loops through players calling `playTurn()`. Each turn: roll → compute new position → if past the end, skip → check board for entity → update player position → if at the last cell, game over. `Dice` is its own class with configurable faces.

**Step 6 — Use a Builder to wire everything**

There are many configurable parts: board size, difficulty, player names, dice faces. A constructor with all of these would be ugly. A builder lets you set each one with a fluent API.

We created `GameBuilder` with defaults (`boardSize=10`, `difficulty=EASY`, `diceFaces=6`). You chain calls like `.boardSize(10).difficulty(Difficulty.EASY).addPlayer("Alice").addPlayer("Bob").build()`. The `build()` method creates the generator from the difficulty, generates the entities, creates the board, creates the dice, wraps the player names into `Player` objects, and returns a ready-to-play `Game`.

---

## UML Diagram

```
        +-------------------+
        |    GameBuilder    |
        |-------------------|
        | - boardSize       |
        | - difficulty      |
        | - playerNames     |
        | - diceFaces       |
        |-------------------|
        | + boardSize(n)    |
        | + difficulty(d)   |
        | + addPlayer(name) |
        | + build() → Game  |
        +---------+---------+
                  |  creates
                  v
        +-------------------+
        |       Game        |
        |-------------------|
        | - board           |---> Board
        | - dice            |---> Dice
        | - players         |---> List<Player>
        | - finished        |
        | - winner          |
        |-------------------|
        | + play()          |
        | - playTurn(player)|
        +-------------------+

+-------------------+    +----------+    +----------+
|      Board        |    |   Dice   |    |  Player  |
|-------------------|    |----------|    |----------|
| - size            |    | - faces  |    | - name   |
| - totalCells      |    | - random |    | - position|
| - entityMap       |    |----------|    +----------+
|-------------------|    | + roll() |
| + getNextPosition |    +----------+
| + getTotalCells   |
+--------+----------+
         |
         | contains
         v
+-------------------+
|   BoardEntity     |
|-------------------|
| - start           |
| - end             |
| - type            |  ("SNAKE" or "LADDER")
+-------------------+

+------------------------+
| BoardEntityGenerator   |  (interface)
|------------------------|
| + generate(boardSize)  |
+-----------+------------+
            |
+-----------+------------+
|RandomBoardEntityGenerator|
|--------------------------|
| - snakeCount, ladderCount|
| + generate(boardSize)    |
+--------------------------+

+------------+
| Difficulty |  (enum)
|------------|
| EASY(4, 6) |  ← 4 snakes, 6 ladders
| HARD(6, 4) |  ← 6 snakes, 4 ladders
+------------+
```

---

## The story in one paragraph

We needed a Snake & Ladder game with configurable board size, multiple players, and difficulty levels. We split it into clean pieces: `Board` knows the layout (a map of cell → snake/ladder), `Dice` rolls, `Player` tracks position, `Game` runs the turn loop. Snake/ladder generation is behind a `BoardEntityGenerator` interface — `RandomBoardEntityGenerator` randomly places them while avoiding the start and end cells. `Difficulty` is an enum that controls how many snakes vs ladders get placed (easy = more ladders, hard = more snakes). `GameBuilder` ties it all together with a fluent API — set board size, pick difficulty, add players, call `build()`, and you get a ready-to-play game. Each turn: roll the dice, move forward, check if you hit a snake or ladder, check if you won. If your roll would take you past the last cell, you stay put. First player to land exactly on the last cell wins.
