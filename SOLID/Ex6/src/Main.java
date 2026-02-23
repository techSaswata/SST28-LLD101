public class Main {
    public static void main(String[] args) {
        System.out.println("=== Notification Demo ===");
        AuditLog audit = new AuditLog();
        NotificationValidator validator = new NotificationValidator();

        Notification n = new Notification("Welcome", "Hello and welcome to SST!", "riya@sst.edu", "9876543210");

        NotificationSender email = new EmailSender(audit, validator);
        NotificationSender sms = new SmsSender(audit, validator);
        NotificationSender wa = new WhatsAppSender(audit, validator);

        email.send(n);
        sms.send(n);
        SendResult waResult = wa.send(n);
        if (!waResult.ok) {
            System.out.println("WA ERROR: " + waResult.errorMessage);
            audit.add("WA failed");
        }

        System.out.println("AUDIT entries=" + audit.size());
    }
}
