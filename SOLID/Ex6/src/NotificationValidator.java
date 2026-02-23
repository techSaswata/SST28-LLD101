public class NotificationValidator {
    public SendResult validateEmail(Notification n) {
        if (n == null || n.email == null || n.email.isBlank()) return SendResult.error("email is required");
        return SendResult.ok();
    }

    public SendResult validatePhone(Notification n) {
        if (n == null || n.phone == null || n.phone.isBlank()) return SendResult.error("phone is required");
        return SendResult.ok();
    }

    public SendResult validateWhatsAppPhone(Notification n) {
        if (n == null || n.phone == null || !n.phone.startsWith("+")) {
            return SendResult.error("phone must start with + and country code");
        }
        return SendResult.ok();
    }
}
