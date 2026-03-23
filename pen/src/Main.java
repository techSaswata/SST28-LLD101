public class Main {
    public static void main(String[] args) {
        System.out.println("=== Pen Demo ===\n");

        RefillablePen gelPen = new RefillablePen("Cello Gel", new GelStrategy(), new Ink("Blue", 2.0));
        gelPen.start();
        gelPen.write("Hello World");
        gelPen.write("Design patterns are fun");
        gelPen.close();

        System.out.println();

        gelPen.refill(new Ink("Red", 1.5));
        gelPen.start();
        gelPen.write("Now writing in red");
        gelPen.close();

        System.out.println();

        Pen ballPen = new Pen("Reynolds", new BallPointStrategy(), new Ink("Black", 1.0));
        ballPen.start();
        ballPen.write("Quick note");
        ballPen.close();

        System.out.println();

        RefillablePen fountainPen = new RefillablePen("Parker", new FountainStrategy(), new Ink("Black", 3.0));
        fountainPen.start();
        fountainPen.write("Elegant handwriting");
        fountainPen.close();

        System.out.println();

        System.out.println("--- Edge cases ---");
        Pen emptyPen = new Pen("Empty Pen", new GelStrategy(), null);
        emptyPen.start();
        emptyPen.write("This should fail — no ink");
        emptyPen.close();

        System.out.println();
        Pen closedPen = new Pen("Closed Pen", new BallPointStrategy(), new Ink("Blue", 1.0));
        closedPen.write("Writing without start — should fail");
        closedPen.close();
    }
}
