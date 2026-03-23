import java.util.List;

public class Game {
    private final Board board;
    private final Dice dice;
    private final List<Player> players;
    private boolean finished;
    private Player winner;

    public Game(Board board, Dice dice, List<Player> players) {
        this.board = board;
        this.dice = dice;
        this.players = players;
        this.finished = false;
    }

    public void play() {
        System.out.println("Game started! Board: " + board.getSize() + "x" + board.getSize());
        System.out.println("Players: " + players);
        System.out.println("Snakes & Ladders: " + board.getEntityMap().values());
        System.out.println();

        while (!finished) {
            for (Player player : players) {
                playTurn(player);
                if (finished) break;
            }
        }

        System.out.println("\n" + winner.getName() + " wins!");
    }

    private void playTurn(Player player) {
        int roll = dice.roll();
        int oldPos = player.getPosition();
        int newPos = oldPos + roll;

        if (newPos > board.getTotalCells()) {
            System.out.println(player.getName() + " rolled " + roll + " — can't move (would go past " + board.getTotalCells() + ")");
            return;
        }

        System.out.println(player.getName() + " rolled " + roll + ": " + oldPos + " → " + newPos);
        newPos = board.getNextPosition(newPos);
        player.setPosition(newPos);

        if (newPos == board.getTotalCells()) {
            finished = true;
            winner = player;
        }
    }

    public Player getWinner() { return winner; }
}
