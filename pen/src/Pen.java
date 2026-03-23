public class Pen {
    private final String name;
    private final WritingStrategy writingStrategy;
    private Ink ink;
    private boolean open;

    public Pen(String name, WritingStrategy writingStrategy, Ink ink) {
        this.name = name;
        this.writingStrategy = writingStrategy;
        this.ink = ink;
        this.open = false;
    }

    public void start() {
        if (open) {
            System.out.println(name + " is already open.");
            return;
        }
        open = true;
        System.out.println(name + " opened. Ready to write.");
    }

    public String write(String content) {
        if (!open) {
            System.out.println(name + " is closed. Call start() first.");
            return null;
        }
        if (ink == null) {
            System.out.println(name + " has no ink. Refill needed.");
            return null;
        }
        String output = writingStrategy.write(content);
        System.out.println(output);
        return output;
    }

    public void close() {
        if (!open) {
            System.out.println(name + " is already closed.");
            return;
        }
        open = false;
        System.out.println(name + " closed.");
    }

    public boolean isOpen() { return open; }
    public String getName() { return name; }
    public Ink getInk() { return ink; }
    public WritingStrategy getWritingStrategy() { return writingStrategy; }

    protected void setInk(Ink ink) { this.ink = ink; }
}
