import java.util.ArrayList;
import java.util.List;

public class GameBuilder {
    private int boardSize = 10;
    private Difficulty difficulty = Difficulty.EASY;
    private List<String> playerNames = new ArrayList<>();
    private int diceFaces = 6;

    public GameBuilder boardSize(int size) { this.boardSize = size; return this; }
    public GameBuilder difficulty(Difficulty difficulty) { this.difficulty = difficulty; return this; }
    public GameBuilder addPlayer(String name) { this.playerNames.add(name); return this; }
    public GameBuilder diceFaces(int faces) { this.diceFaces = faces; return this; }

    public Game build() {
        if (playerNames.size() < 2) {
            throw new IllegalStateException("Need at least 2 players");
        }

        BoardEntityGenerator generator = new RandomBoardEntityGenerator(difficulty.getSnakes(), difficulty.getLadders());
        List<BoardEntity> entities = generator.generate(boardSize);
        Board board = new Board(boardSize, entities);
        Dice dice = new Dice(diceFaces);
        List<Player> players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        return new Game(board, dice, players);
    }
}
