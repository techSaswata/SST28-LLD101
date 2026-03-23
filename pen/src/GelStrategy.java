public class GelStrategy implements WritingStrategy {
    @Override
    public String write(String content) {
        return "[Gel] " + content;
    }

    @Override
    public String getType() { return "GEL"; }
}
