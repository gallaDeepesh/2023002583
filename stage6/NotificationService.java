import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NotificationService {

    public static double calculateScore(Notification n) {

        int weight = 0;

        switch (n.getType()) {

            case "Placement":
                weight = 300;
                break;

            case "Result":
                weight = 200;
                break;

            case "Event":
                weight = 100;
                break;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss"
                );

        LocalDateTime notificationTime =
                LocalDateTime.parse(
                        n.getTimestamp(),
                        formatter
                );

        LocalDateTime now =
                LocalDateTime.now();

        long hours =
                ChronoUnit.HOURS.between(
                        notificationTime,
                        now
                );

        double recencyBonus =
                Math.max(0, 100 - hours);

        return weight + recencyBonus;
    }
}