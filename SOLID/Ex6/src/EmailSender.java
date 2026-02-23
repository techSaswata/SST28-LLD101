public class EmailSender extends NotificationSender {
    public EmailSender(AuditLog audit, NotificationValidator validator) { super(audit, validator); }

    @Override
    public SendResult send(Notification n) {
        SendResult validation = validator.validateEmail(n);
        if (!validation.ok) return validation;
        String body = n.body == null ? "" : n.body;
        String subject = n.subject == null ? "" : n.subject;
        System.out.println("EMAIL -> to=" + n.email + " subject=" + subject + " body=" + body);
        audit.add("email sent");
        return SendResult.ok();
    }
}
