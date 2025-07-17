package creditcarddetector.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "detection_events")
public class DetectionEvent {

    @Id
    private String id;
    private String messageId;
    private String sender;
    private List<String> detectedCardNumbers;
    private long timestamp;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public List<String> getDetectedCardNumbers() { return detectedCardNumbers; }
    public void setDetectedCardNumbers(List<String> detectedCardNumbers) { this.detectedCardNumbers = detectedCardNumbers; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

