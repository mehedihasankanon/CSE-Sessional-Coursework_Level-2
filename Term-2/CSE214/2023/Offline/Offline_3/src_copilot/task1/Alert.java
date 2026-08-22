package task1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an emergency disaster alert payload.
 */
public class Alert {
    private final String title;
    private final DisasterCategory category;
    private final String location;
    private final String severity;
    private final String safetyInstructions;
    private final String timestamp;

    public Alert(String title, DisasterCategory category, String location, String severity, String safetyInstructions) {
        this.title = title;
        this.category = category;
        this.location = location;
        this.severity = severity;
        this.safetyInstructions = safetyInstructions;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getTitle() {
        return title;
    }

    public DisasterCategory getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getSeverity() {
        return severity;
    }

    public String getSafetyInstructions() {
        return safetyInstructions;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format(
            "[%s ALERT | %s] Title: %s | Location: %s | Severity: %s\n  --> Instructions: %s (Time: %s)",
            category, category, title, location, severity, safetyInstructions, timestamp
        );
    }
}
