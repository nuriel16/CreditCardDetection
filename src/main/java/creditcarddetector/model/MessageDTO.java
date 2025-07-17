package creditcarddetector.model;

import java.util.List;

public class MessageDTO {
    private String id;
    private String sender;
    private List<String> recipients;
    private String subject;
    private String body;
    private long sentTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public long getSentTime() { return sentTime; }
    public void setSentTime(long sentTime) { this.sentTime = sentTime; }
}
