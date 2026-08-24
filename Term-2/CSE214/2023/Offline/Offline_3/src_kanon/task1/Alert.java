package task1;

import java.time.LocalDateTime;

public class Alert {
    private final String title;
    private final Category cat;
    private final String location;
    private final int severityLevel;
    private final String instructions;

    private Alert(Builder builder) {
        this.title = builder.title;
        this.cat = builder.cat;
        this.location = builder.location;
        this.severityLevel = builder.severityLevel;
        this.instructions = builder.instructions;
    }

    public Category getCat() {
        return cat;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Location: %s | Severity: %d | Instructions: %s",
                title, cat, location, severityLevel, instructions);
    }

    public static class Builder {
        private final Category cat;
        private String title;
        private String location = "Unknown";
        private int severityLevel = 1;
        private String instructions = "No specific instructions.";

        public Builder(Category cat) {
            this.cat = cat;
            this.title = alertMsg(cat);
        }

        public Builder title(String title) {
            this.title = customTitle(title);
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder severityLevel(int severityLevel) {
            this.severityLevel = severityLevel;
            return this;
        }

        public Builder instructions(String instructions) {
            this.instructions = instructions;
            return this;
        }

        public Alert build() {
            return new Alert(this);
        }
    }

    private static String getCurrentTime() {
        return LocalDateTime.now().toString();
    }

    private static String alertMsg(Category alert) {
        return "[ " + getCurrentTime() + " ]: " + alert.toString() + ' ' + "ALERT";
    }

    private static String customTitle(String title) {
        return "[ " + getCurrentTime() + " ]: " + title;
    }

    public static Alert generateAlert(Category cat) {
        // Generic alert generation fr fast prototyping
        return new Alert.Builder(cat)
                .location("Dhaka Division")
                .severityLevel(8)
                .instructions("Evacuate immediately to higher ground.")
                .build();
    }
}