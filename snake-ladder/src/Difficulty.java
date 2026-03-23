public enum Difficulty {
    EASY(4, 6),
    HARD(6, 4);

    private final int snakes;
    private final int ladders;

    Difficulty(int snakes, int ladders) {
        this.snakes = snakes;
        this.ladders = ladders;
    }

    public int getSnakes() { return snakes; }
    public int getLadders() { return ladders; }
}
