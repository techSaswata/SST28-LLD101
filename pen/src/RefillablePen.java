public class RefillablePen extends Pen implements Refillable {

    public RefillablePen(String name, WritingStrategy writingStrategy, Ink ink) {
        super(name, writingStrategy, ink);
    }

    @Override
    public void refill(Ink newInk) {
        setInk(newInk);
        System.out.println(getName() + " refilled with " + newInk.getColor() + " ink (" + newInk.getCapacity() + " ml).");
    }
}
