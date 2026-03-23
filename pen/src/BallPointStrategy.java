public class BallPointStrategy implements WritingStrategy {
    @Override
    public String write(String content) {
        return "[BallPoint] " + content;
    }

    @Override
    public String getType() { return "BALL_POINT"; }
}
