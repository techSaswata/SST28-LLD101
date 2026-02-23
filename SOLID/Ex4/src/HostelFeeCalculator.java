public class HostelFeeCalculator {
    private final FakeBookingRepo repo;
    private final RoomRateProvider roomRates;
    private final AddOnRateProvider addOnRates;

    public HostelFeeCalculator(FakeBookingRepo repo) {
        this(repo, DefaultRoomRateProvider.create(), DefaultAddOnRateProvider.create());
    }

    public HostelFeeCalculator(FakeBookingRepo repo, RoomRateProvider roomRates, AddOnRateProvider addOnRates) {
        this.repo = repo;
        this.roomRates = roomRates;
        this.addOnRates = addOnRates;
    }

    public void process(BookingRequest req) {
        Money monthly = calculateMonthly(req);
        Money deposit = new Money(5000.00);

        ReceiptPrinter.print(req, monthly, deposit);

        String bookingId = "H-" + (7000 + new java.util.Random(1).nextInt(1000)); // deterministic-ish
        repo.save(bookingId, req, monthly, deposit);
    }

    private Money calculateMonthly(BookingRequest req) {
        Money base = roomRates.baseFor(req.roomType);
        Money add = new Money(0.0);
        for (AddOn a : req.addOns) {
            add = add.plus(addOnRates.feeFor(a));
        }
        return base.plus(add);
    }
}
