import com.example.tickets.IncidentTicket;
import com.example.tickets.TicketService;

import java.util.List;

/**
 * Demo showing immutability in action.
 *
 * - Tickets are built via Builder; no setters exist.
 * - "Updates" produce new ticket instances; the original is unchanged.
 * - External modifications to the tags list have no effect on the ticket.
 */
public class TryIt {

    public static void main(String[] args) {
        TicketService service = new TicketService();

        // 1. Create a ticket via the service (uses Builder internally)
        IncidentTicket t1 = service.createTicket("TCK-1001", "reporter@example.com", "Payment failing on checkout");
        System.out.println("Created t1: " + t1);

        // 2. "Assign" returns a NEW ticket — t1 stays unchanged
        IncidentTicket t2 = service.assign(t1, "agent@example.com");
        System.out.println("\nt2 (assigned): " + t2);
        System.out.println("t1 unchanged : " + t1);

        // 3. "Escalate" returns a NEW ticket — t2 stays unchanged
        IncidentTicket t3 = service.escalateToCritical(t2);
        System.out.println("\nt3 (escalated): " + t3);
        System.out.println("t2 unchanged  : " + t2);

        // 4. External tag mutation has NO effect (defensive copy + unmodifiable list)
        List<String> tags = t3.getTags();
        try {
            tags.add("HACKED_FROM_OUTSIDE");
            System.out.println("\nERROR: should not reach here");
        } catch (UnsupportedOperationException e) {
            System.out.println("\nExternal tag mutation blocked (UnsupportedOperationException)");
        }
        System.out.println("t3 still safe: " + t3);

        // 5. Direct Builder usage with optional fields
        IncidentTicket custom = new IncidentTicket.Builder("BUG-42", "dev@company.com", "Login page 500")
                .description("Server error on POST /login")
                .priority("HIGH")
                .slaMinutes(60)
                .source("WEBHOOK")
                .customerVisible(true)
                .addTag("AUTH")
                .addTag("URGENT")
                .build();
        System.out.println("\nCustom ticket: " + custom);
    }
}
