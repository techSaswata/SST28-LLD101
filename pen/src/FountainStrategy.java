public class FountainStrategy implements WritingStrategy {
    @Override
    public String write(String content) {
        return "[Fountain] " + content;
    }

    @Override
    public String getType() { return "FOUNTAIN"; }
}
