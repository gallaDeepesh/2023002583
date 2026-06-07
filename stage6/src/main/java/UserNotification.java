import com.fasterxml.jackson.annotation.JsonProperty;

public class UserNotification {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Timestamp")
    private String timestamp;

    private double score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}