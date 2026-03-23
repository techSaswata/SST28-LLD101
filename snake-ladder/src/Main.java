public class Main {
    public static void main(String[] args) {
        System.out.println("=== Snake & Ladder ===\n");

        Game game = new GameBuilder()
                .boardSize(10)
                .difficulty(Difficulty.EASY)
                .addPlayer("Alice")
                .addPlayer("Bob")
                .addPlayer("Charlie")
                .build();

        game.play();
    }
}
