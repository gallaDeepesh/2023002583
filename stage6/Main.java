import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class Main {

    private static final String TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiJkZ2FsbGFAZ2l0YW0uaW4iLCJleHAiOjE3ODA4MTc3NzUsImlhdCI6MTc4MDgxNjg3NSwiaXNzIjoiQWZmb3JkIE1lZGljYWwgVGVjaG5vbG9naWVzIFByaXZhdGUgTGltaXRlZCIsImp0aSI6ImJhMTYxYWIwLTYwNTctNGM2Yi1iMDJmLWU3YjhjMGYwZWFlYyIsImxvY2FsZSI6ImVuLUlOIiwibmFtZSI6ImdhbGxhIGRlZXBlc2giLCJzdWIiOiJkZjNjODYxYy03OTRjLTQzNjgtOWJmNS02ZGJiMjM0ZGMyOWUifSwiZW1haWwiOiJkZ2FsbGFAZ2l0YW0uaW4iLCJuYW1lIjoiZ2FsbGEgZGVlcGVzaCIsInJvbGxObyI6IjIwMjMwMDI1ODMiLCJhY2Nlc3NDb2RlIjoid2dLdGdaIiwiY2xpZW50SUQiOiJkZjNjODYxYy03OTRjLTQzNjgtOWJmNS02ZGJiMjM0ZGMyOWUiLCJjbGllbnRTZWNyZXQiOiJRUFp0ZUdWekhNakpWZFpNIn0.EdLou55FvzJud1kdu_S_7H_UVRy5a-CepC8a9jpcuQc";

    public static void main(String[] args)
            throws Exception {

        URL url = new URL(
                "http://4.224.186.213/evaluation-service/notifications"
        );

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        conn.setRequestProperty(
                "Authorization",
                "Bearer " + TOKEN
        );

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()
                        )
                );

        StringBuilder response =
                new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        ObjectMapper mapper =
                new ObjectMapper();

        NotificationResponse result =
                mapper.readValue(
                        response.toString(),
                        NotificationResponse.class
                );

        List<Notification> notifications =
                result.getNotifications();

        for (Notification n : notifications) {

            n.setScore(
                    NotificationService
                            .calculateScore(n)
            );
        }

        notifications.sort(
                Comparator.comparingDouble(
                        Notification::getScore
                ).reversed()
        );

        System.out.println(
                "Top 10 Notifications"
        );

        for (int i = 0;
             i < Math.min(10,
                     notifications.size());
             i++) {

            Notification n =
                    notifications.get(i);

            System.out.println(
                    (i + 1) + ". "
                            + n.getType()
                            + " | "
                            + n.getMessage()
                            + " | Score = "
                            + n.getScore()
            );
        }
    }
}