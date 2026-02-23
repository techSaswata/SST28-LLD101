public class SmsSender extends NotificationSender {
    public SmsSender(AuditLog audit, NotificationValidator validator) { super(audit, validator); }

    @Override
    public SendResult send(Notification n) {
        SendResult validation = validator.validatePhone(n);
        if (!validation.ok) return validation;
        String body = n.body == null ? "" : n.body;
        System.out.println("SMS -> to=" + n.phone + " body=" + body);
        audit.add("sms sent");
        return SendResult.ok();
    }
}
