public class WhatsAppSender extends NotificationSender {
    public WhatsAppSender(AuditLog audit, NotificationValidator validator) { super(audit, validator); }

    @Override
    public SendResult send(Notification n) {
        SendResult validation = validator.validateWhatsAppPhone(n);
        if (!validation.ok) return validation;
        String body = n.body == null ? "" : n.body;
        System.out.println("WA -> to=" + n.phone + " body=" + body);
        audit.add("wa sent");
        return SendResult.ok();
    }
}
