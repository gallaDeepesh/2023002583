import javax.management.Notification;
import java.util.List;

public class NotificationResponse {

    private List<UserNotification> notifications;

    public List<UserNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<UserNotification> notifications) {
        this.notifications = notifications;
    }
}