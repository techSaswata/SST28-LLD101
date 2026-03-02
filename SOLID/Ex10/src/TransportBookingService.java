public class TransportBookingService {
    private final DistanceCalculatorService distanceCalculator;
    private final DriverAllocatorService driverAllocator;
    private final PaymentGatewayService paymentGateway;
    private final FareCalculator fareCalculator;

    public TransportBookingService(DistanceCalculatorService distanceCalculator,
                                   DriverAllocatorService driverAllocator,
                                   PaymentGatewayService paymentGateway,
                                   FareCalculator fareCalculator) {
        this.distanceCalculator = distanceCalculator;
        this.driverAllocator = driverAllocator;
        this.paymentGateway = paymentGateway;
        this.fareCalculator = fareCalculator;
    }

    public void book(TripRequest req) {
        double km = distanceCalculator.km(req.from, req.to);
        System.out.println("DistanceKm=" + km);

        String driver = driverAllocator.allocate(req.studentId);
        System.out.println("Driver=" + driver);

        double fare = fareCalculator.fare(km);
        String txn = paymentGateway.charge(req.studentId, fare);
        System.out.println("Payment=PAID txn=" + txn);

        BookingReceipt r = new BookingReceipt("R-501", fare);
        System.out.println("RECEIPT: " + r.id + " | fare=" + String.format("%.2f", r.fare));
    }
}
