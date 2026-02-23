public abstract class NotificationSender {
    protected final AuditLog audit;
    protected final NotificationValidator validator;
    protected NotificationSender(AuditLog audit, NotificationValidator validator) {
        this.audit = audit;
        this.validator = validator;
    }
    public abstract SendResult send(Notification n);
}
