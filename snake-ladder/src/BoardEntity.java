public class BoardEntity {
    private final int start;
    private final int end;
    private final String type;

    public BoardEntity(int start, int end, String type) {
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public int getStart() { return start; }
    public int getEnd() { return end; }
    public String getType() { return type; }

    @Override
    public String toString() { return type + "(" + start + "->" + end + ")"; }
}
